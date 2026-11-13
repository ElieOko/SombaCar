package t3digitalgroup.vehnixauto.server.app.rental.application.services

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.car.application.services.CarListingService
import t3digitalgroup.vehnixauto.server.app.message.application.services.SupportThreadService
import t3digitalgroup.vehnixauto.server.app.message.domain.models.request.SupportThreadRequest
import t3digitalgroup.vehnixauto.server.app.moto.application.services.MotoListingService
import t3digitalgroup.vehnixauto.server.app.rental.domain.models.RentalQuoteRequest
import t3digitalgroup.vehnixauto.server.app.rental.domain.models.RentalRequest
import t3digitalgroup.vehnixauto.server.app.rental.domain.models.RentalRequestCreate
import t3digitalgroup.vehnixauto.server.app.rental.infrastructure.entities.RentalRequestEntity
import t3digitalgroup.vehnixauto.server.app.rental.infrastructure.repositories.RentalRequestRepository
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.repositories.UserRepository
import t3digitalgroup.vehnixauto.server.utils.*
import java.time.LocalDateTime

@Service
@Profile(Mode.DEV)
class RentalRequestService(
    private val repository: RentalRequestRepository,
    private val carListingService: CarListingService,
    private val motoListingService: MotoListingService,
    private val supportThreadService: SupportThreadService,
    private val userRepository: UserRepository,
) {
    suspend fun create(clientUserId: Long, request: RentalRequestCreate): RentalRequest {
        if (request.endDate.isBefore(request.startDate)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "La date de fin doit être après la date de début.")
        }
        val driverUserId = when (request.vehicleType) {
            VehicleKind.CAR -> {
                val listing = carListingService.findById(request.vehicleListingId)
                if (listing.listingType != ListingType.RENT.name) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette annonce n'est pas une location.")
                }
                listing.driverUserId
            }
            VehicleKind.MOTO -> {
                val listing = motoListingService.findById(request.vehicleListingId)
                if (listing.listingType != ListingType.RENT.name) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette annonce n'est pas une location.")
                }
                listing.driverUserId
            }
        }

        val details = buildString {
            append("Du ${request.startDate} au ${request.endDate}. ")
            request.destinations?.let { append("Destinations: $it. ") }
            request.plannedRoute?.let { append("Trajet: $it. ") }
            request.motive?.let { append("Motif: $it. ") }
            request.additionalInfo?.let { append(it) }
        }
        val thread = runCatching {
            supportThreadService.openThread(
                SupportThreadRequest(
                    userId = clientUserId,
                    subject = "Demande de location ${request.vehicleType} #${request.vehicleListingId}",
                    partSought = "Location véhicule",
                    compatibleBrand = null,
                    compatibleModel = null,
                    initialMessage = details.ifBlank { "Nouvelle demande de location." },
                )
            )
        }.getOrNull()

        val saved = repository.save(
            RentalRequestEntity(
                vehicleType = request.vehicleType.name,
                vehicleListingId = request.vehicleListingId,
                clientUserId = clientUserId,
                driverUserId = driverUserId,
                startDate = request.startDate,
                endDate = request.endDate,
                durationUnit = request.durationUnit.name,
                durationValue = request.durationValue,
                destinations = request.destinations,
                plannedRoute = request.plannedRoute,
                motive = request.motive,
                additionalInfo = request.additionalInfo,
                supportThreadId = thread?.threadId,
            )
        )
        return saved.toDomain()
    }

    suspend fun findById(id: Long): RentalRequest =
        repository.findById(id)?.toDomain()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Demande de location introuvable.")

    suspend fun findMine(userId: Long): List<RentalRequest> =
        repository.findByClientUserId(userId).map { it.toDomain() }.toList()

    suspend fun findAll(status: RentalRequestStatus? = null): List<RentalRequest> {
        val flow = if (status != null) repository.findByStatus(status.name) else repository.findAllOrdered()
        return flow.map { it.toDomain() }.toList()
    }

    suspend fun updateQuote(id: Long, request: RentalQuoteRequest): RentalRequest {
        val entity = repository.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Demande de location introuvable.")
        entity.quotedAmount = request.quotedAmount
        entity.devise = request.devise
        request.paymentLink?.let { entity.paymentLink = it }
        request.paymentReference?.let { entity.paymentReference = it }
        request.driverUserId?.let { driverId ->
            userRepository.findById(driverId)
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Chauffeur introuvable.")
            entity.driverUserId = driverId
        }
        request.status?.let { entity.status = it.name }
        entity.updatedAt = LocalDateTime.now()
        return repository.save(entity).toDomain()
    }

    suspend fun updateStatus(id: Long, status: RentalRequestStatus): RentalRequest {
        val entity = repository.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Demande de location introuvable.")
        entity.status = status.name
        entity.updatedAt = LocalDateTime.now()
        if (status == RentalRequestStatus.PAID || status == RentalRequestStatus.CONFIRMED) {
            when (entity.vehicleType) {
                VehicleKind.CAR.name -> carListingService.updateStatus(entity.vehicleListingId, ListingStatus.RENTED)
                VehicleKind.MOTO.name -> motoListingService.updateStatus(entity.vehicleListingId, ListingStatus.RENTED)
            }
        }
        return repository.save(entity).toDomain()
    }
}

fun RentalRequestEntity.toDomain() = RentalRequest(
    id = id,
    vehicleType = VehicleKind.valueOf(vehicleType),
    vehicleListingId = vehicleListingId,
    clientUserId = clientUserId,
    driverUserId = driverUserId,
    startDate = startDate,
    endDate = endDate,
    durationUnit = runCatching { RentalDurationUnit.valueOf(durationUnit) }.getOrDefault(RentalDurationUnit.DAYS),
    durationValue = durationValue,
    destinations = destinations,
    plannedRoute = plannedRoute,
    motive = motive,
    additionalInfo = additionalInfo,
    quotedAmount = quotedAmount,
    devise = devise,
    paymentLink = paymentLink,
    paymentReference = paymentReference,
    status = runCatching { RentalRequestStatus.valueOf(status) }.getOrDefault(RentalRequestStatus.PENDING),
    supportThreadId = supportThreadId,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
