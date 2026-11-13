package t3digitalgroup.vehnixauto.server.app.tracking.application.services

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.car.infrastructure.repositories.CarListingRepository
import t3digitalgroup.vehnixauto.server.app.moto.infrastructure.repositories.MotoListingRepository
import t3digitalgroup.vehnixauto.server.app.tracking.domain.models.*
import t3digitalgroup.vehnixauto.server.app.tracking.infrastructure.entities.VehicleTrackingEventEntity
import t3digitalgroup.vehnixauto.server.app.tracking.infrastructure.repositories.VehicleTrackingEventRepository
import t3digitalgroup.vehnixauto.server.utils.Mode
import t3digitalgroup.vehnixauto.server.utils.TrackingTag
import t3digitalgroup.vehnixauto.server.utils.VehicleKind
import t3digitalgroup.vehnixauto.server.utils.validateGeoCoordinates
import java.time.LocalDateTime

@Service
@Profile(Mode.DEV)
class TrackingService(
    private val repository: VehicleTrackingEventRepository,
    private val carListingRepository: CarListingRepository,
    private val motoListingRepository: MotoListingRepository,
    private val bridge: TrackingEventBridge,
) {
    suspend fun publish(userId: Long?, request: TrackingEventRequest): VehicleTrackingEvent {
        validateGeoCoordinates(request.latitude, request.longitude)
        ensureVehicleExists(request.vehicleType, request.vehicleId)
        val normalizedTag = normalizeTag(request.tag)
        val entity = VehicleTrackingEventEntity(
            vehicleType = request.vehicleType.name,
            vehicleId = request.vehicleId,
            userId = userId,
            driverUserId = request.driverUserId,
            latitude = request.latitude,
            longitude = request.longitude,
            tag = normalizedTag,
            isActive = request.isActive,
            message = request.message,
            rentalRequestId = request.rentalRequestId,
        )
        val saved = repository.save(entity)
        val event = saved.toDomain()
        bridge.broadcast(event)
        return event
    }

    suspend fun publishCar(userId: Long?, dto: TrackingCarDTO): TrackingCarDTO {
        val carId = dto.carId ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "carId obligatoire.")
        val event = publish(
            userId = userId ?: dto.userId,
            request = TrackingEventRequest(
                vehicleType = VehicleKind.CAR,
                vehicleId = carId,
                latitude = dto.latitude,
                longitude = dto.longitude,
                tag = dto.tag,
                isActive = dto.isActive,
                message = dto.message,
                driverUserId = dto.userId,
            ),
        )
        return TrackingCarDTO(
            id = event.id,
            userId = event.userId,
            carId = event.vehicleId,
            latitude = event.latitude,
            longitude = event.longitude,
            tag = event.tag,
            isActive = event.isActive,
            message = event.message,
            timestamp = event.createdAt,
        )
    }

    suspend fun publishMoto(userId: Long?, dto: TrackingMotoDTO): TrackingMotoDTO {
        val motoId = dto.motoId ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "motoId obligatoire.")
        val event = publish(
            userId = userId ?: dto.userId,
            request = TrackingEventRequest(
                vehicleType = VehicleKind.MOTO,
                vehicleId = motoId,
                latitude = dto.latitude,
                longitude = dto.longitude,
                tag = dto.tag,
                isActive = dto.isActive,
                message = dto.message,
                driverUserId = dto.userId,
            ),
        )
        return TrackingMotoDTO(
            id = event.id,
            userId = event.userId,
            motoId = event.vehicleId,
            latitude = event.latitude,
            longitude = event.longitude,
            tag = event.tag,
            isActive = event.isActive,
            message = event.message,
            timestamp = event.createdAt,
        )
    }

    suspend fun history(vehicleType: VehicleKind, vehicleId: Long): List<VehicleTrackingEvent> =
        repository.findByVehicle(vehicleType.name, vehicleId).map { it.toDomain() }.toList()

    suspend fun latest(vehicleType: VehicleKind, vehicleId: Long): VehicleTrackingEvent? =
        repository.findLatestActive(vehicleType.name, vehicleId)?.toDomain()

    suspend fun historyByRental(rentalRequestId: Long): List<VehicleTrackingEvent> =
        repository.findByRentalRequestId(rentalRequestId).map { it.toDomain() }.toList()

    private suspend fun ensureVehicleExists(type: VehicleKind, id: Long) {
        when (type) {
            VehicleKind.CAR -> carListingRepository.findById(id)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Annonce voiture introuvable.")
            VehicleKind.MOTO -> motoListingRepository.findById(id)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Annonce moto introuvable.")
        }
    }

    private fun normalizeTag(tag: String?): String {
        if (tag.isNullOrBlank()) return "Depart"
        val upper = tag.trim().uppercase().replace(' ', '_')
        return when (upper) {
            "DEPART", "DÉPART" -> "Depart"
            "EN_ROUTE", "ENROUTE", "EN-ROUTE" -> "En route"
            "ARRIVE", "ARRIVÉ", "ARRIVEE", "ARRIVÉE" -> "Arrive"
            "PAUSE" -> "Pause"
            "FIN", "END" -> "Fin"
            else -> tag.trim()
        }.also {
            // Keep TrackingTag enum aligned for known values
            runCatching {
                when (it) {
                    "Depart" -> TrackingTag.DEPART
                    "En route" -> TrackingTag.EN_ROUTE
                    "Arrive" -> TrackingTag.ARRIVE
                    "Pause" -> TrackingTag.PAUSE
                    "Fin" -> TrackingTag.FIN
                }
            }
        }
    }
}

fun VehicleTrackingEventEntity.toDomain() = VehicleTrackingEvent(
    id = id,
    vehicleType = VehicleKind.valueOf(vehicleType),
    vehicleId = vehicleId,
    userId = userId,
    driverUserId = driverUserId,
    latitude = latitude,
    longitude = longitude,
    tag = tag,
    isActive = isActive,
    message = message,
    rentalRequestId = rentalRequestId,
    createdAt = createdAt,
)
