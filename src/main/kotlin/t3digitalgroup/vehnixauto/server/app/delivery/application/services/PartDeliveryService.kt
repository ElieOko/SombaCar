package t3digitalgroup.vehnixauto.server.app.delivery.application.services

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.delivery.domain.models.*
import t3digitalgroup.vehnixauto.server.app.delivery.infrastructure.entities.PartDeliveryEntity
import t3digitalgroup.vehnixauto.server.app.delivery.infrastructure.entities.PartDeliveryTrackingEventEntity
import t3digitalgroup.vehnixauto.server.app.delivery.infrastructure.repositories.PartDeliveryRepository
import t3digitalgroup.vehnixauto.server.app.delivery.infrastructure.repositories.PartDeliveryTrackingRepository
import t3digitalgroup.vehnixauto.server.app.tools.infrastructure.repositories.PartListingRepository
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.repositories.UserRepository
import t3digitalgroup.vehnixauto.server.utils.DeliveryStatus
import t3digitalgroup.vehnixauto.server.utils.Mode
import t3digitalgroup.vehnixauto.server.utils.validateGeoCoordinates
import java.time.LocalDateTime

@Service
@Profile(Mode.DEV)
class PartDeliveryService(
    private val repository: PartDeliveryRepository,
    private val trackingRepository: PartDeliveryTrackingRepository,
    private val partListingRepository: PartListingRepository,
    private val userRepository: UserRepository,
    private val messagingTemplate: SimpMessagingTemplate,
) {
    suspend fun create(buyerUserId: Long, request: PartDeliveryCreate): PartDelivery {
        validateGeoCoordinates(request.latitude, request.longitude)
        val sellerId = request.partListingId?.let { partId ->
            val part = partListingRepository.findById(partId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Annonce pièce introuvable.")
            part.userId
        }
        val saved = repository.save(
            PartDeliveryEntity(
                partListingId = request.partListingId,
                buyerUserId = buyerUserId,
                sellerUserId = sellerId,
                deliveryAddress = request.deliveryAddress,
                city = request.city,
                latitude = request.latitude,
                longitude = request.longitude,
                notes = request.notes,
                paymentReference = request.paymentReference,
            )
        )
        return saved.toDomain()
    }

    suspend fun findById(id: Long): PartDelivery =
        repository.findById(id)?.toDomain()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Livraison introuvable.")

    suspend fun findMine(userId: Long): List<PartDelivery> =
        repository.findByBuyerUserId(userId).map { it.toDomain() }.toList()

    suspend fun findAll(status: DeliveryStatus? = null): List<PartDelivery> {
        val flow = if (status != null) repository.findByStatus(status.name) else repository.findAllOrdered()
        return flow.map { it.toDomain() }.toList()
    }

    suspend fun assignCourier(id: Long, courierUserId: Long): PartDelivery {
        val entity = repository.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Livraison introuvable.")
        userRepository.findById(courierUserId)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Livreur introuvable.")
        entity.courierUserId = courierUserId
        entity.status = DeliveryStatus.ASSIGNED.name
        entity.updatedAt = LocalDateTime.now()
        return repository.save(entity).toDomain()
    }

    suspend fun updateStatus(id: Long, status: DeliveryStatus): PartDelivery {
        val entity = repository.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Livraison introuvable.")
        entity.status = status.name
        entity.updatedAt = LocalDateTime.now()
        return repository.save(entity).toDomain()
    }

    suspend fun track(id: Long, userId: Long?, request: PartDeliveryTrackingRequest): PartDeliveryTrackingEvent {
        val delivery = repository.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Livraison introuvable.")
        validateGeoCoordinates(request.latitude, request.longitude)
        val event = trackingRepository.save(
            PartDeliveryTrackingEventEntity(
                deliveryId = id,
                userId = userId,
                latitude = request.latitude,
                longitude = request.longitude,
                tag = request.tag ?: "Depart",
                isActive = request.isActive,
                message = request.message,
            )
        )
        if (delivery.status == DeliveryStatus.ASSIGNED.name || delivery.status == DeliveryStatus.PENDING.name) {
            delivery.status = DeliveryStatus.IN_TRANSIT.name
            delivery.updatedAt = LocalDateTime.now()
            repository.save(delivery)
        }
        val domain = event.toDomain()
        messagingTemplate.convertAndSend("/topic/tracking/delivery/$id", domain)
        return domain
    }

    suspend fun trackingHistory(id: Long): List<PartDeliveryTrackingEvent> =
        trackingRepository.findByDeliveryId(id).map { it.toDomain() }.toList()
}

fun PartDeliveryEntity.toDomain() = PartDelivery(
    id = id,
    partListingId = partListingId,
    buyerUserId = buyerUserId,
    sellerUserId = sellerUserId,
    courierUserId = courierUserId,
    deliveryAddress = deliveryAddress,
    city = city,
    latitude = latitude,
    longitude = longitude,
    notes = notes,
    paymentReference = paymentReference,
    status = runCatching { DeliveryStatus.valueOf(status) }.getOrDefault(DeliveryStatus.PENDING),
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun PartDeliveryTrackingEventEntity.toDomain() = PartDeliveryTrackingEvent(
    id = id,
    deliveryId = deliveryId,
    userId = userId,
    latitude = latitude,
    longitude = longitude,
    tag = tag,
    isActive = isActive,
    message = message,
    createdAt = createdAt,
)
