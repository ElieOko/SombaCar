package t3digitalgroup.vehnixauto.server.app.car.application.services

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.car.domain.models.*
import t3digitalgroup.vehnixauto.server.app.car.domain.models.request.*
import t3digitalgroup.vehnixauto.server.app.car.infrastructure.mapper.*
import t3digitalgroup.vehnixauto.server.app.car.infrastructure.repositories.*
import t3digitalgroup.vehnixauto.server.utils.*
import java.time.LocalDateTime

@Service
@Profile(Mode.DEV)
class CarListingService(
    private val listingRepository: CarListingRepository,
    private val carModelRepository: CarModelRepository,
    private val carImageService: CarImageService,
    private val carDocumentService: CarDocumentService,
) {
    suspend fun create(request: CarListingRequest): CarListing {
        validateListingRequest(request)
        val carModel = carModelRepository.findById(request.carModelId)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Modèle de voiture introuvable.")

        val entity = CarListing(
            userId = request.userId,
            carModelId = request.carModelId,
            listingType = request.listingType.name,
            year = request.year,
            isElectric = request.isElectric,
            mileageKm = request.mileageKm,
            condition = request.condition.name,
            plateNumber = request.plateNumber,
            color = request.color,
            seats = request.seats,
            price = request.price,
            rentPricePerDay = request.rentPricePerDay,
            exchangeDescription = request.exchangeDescription,
            description = request.description,
            city = request.city,
            country = request.country
        ).toEntity()

        val saved = listingRepository.save(entity)
        saved.listingId?.let { listingId ->
            request.documentIds?.takeIf { it.isNotEmpty() }?.let { documentIds ->
                carDocumentService.linkDocuments(listingId, documentIds)
            }
        }
        return enrichListings(
            listOf(saved.toDomain(carModel.toDomain())),
            includeDocuments = !request.documentIds.isNullOrEmpty(),
        ).first()
    }

    suspend fun findById(id: Long, includeDocuments: Boolean = false): CarListing {
        val listing = listingRepository.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Annonce introuvable.")
        val carModel = carModelRepository.findById(listing.carModelId)?.toDomain()
        val listings = enrichListings(listOf(listing.toDomain(carModel)), includeDocuments)
        return listings.first()
    }

    suspend fun findByUserId(userId: Long, includeDocuments: Boolean = false): List<CarListing> {
        val listings = listingRepository.findByUserId(userId).map { listing ->
            val carModel = carModelRepository.findById(listing.carModelId)?.toDomain()
            listing.toDomain(carModel)
        }.toList()
        return enrichListings(listings, includeDocuments)
    }

    suspend fun findByListingType(listingType: ListingType, includeDocuments: Boolean = false): List<CarListing> {
        val listings = listingRepository.findActiveByListingType(listingType.name).map { listing ->
            val carModel = carModelRepository.findById(listing.carModelId)?.toDomain()
            listing.toDomain(carModel)
        }.toList()
        return enrichListings(listings, includeDocuments)
    }

    suspend fun findByCarModelId(carModelId: Long, includeDocuments: Boolean = false): List<CarListing> {
        val listings = listingRepository.findActiveByCarModelId(carModelId).map { listing ->
            val carModel = carModelRepository.findById(listing.carModelId)?.toDomain()
            listing.toDomain(carModel)
        }.toList()
        return enrichListings(listings, includeDocuments)
    }

    suspend fun findByElectricAndCondition(
        isElectric: Boolean,
        condition: ItemCondition,
        includeDocuments: Boolean = false,
    ): List<CarListing> {
        val listings = listingRepository.findActiveByElectricAndCondition(isElectric, condition.name).map { listing ->
            val carModel = carModelRepository.findById(listing.carModelId)?.toDomain()
            listing.toDomain(carModel)
        }.toList()
        return enrichListings(listings, includeDocuments)
    }

    suspend fun updateStatus(listingId: Long, status: ListingStatus): CarListing {
        val entity = listingRepository.findById(listingId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Annonce introuvable.")
        entity.status = status.name
        entity.updatedAt = LocalDateTime.now()
        val saved = listingRepository.save(entity)
        val carModel = carModelRepository.findById(saved.carModelId)?.toDomain()
        return enrichListings(listOf(saved.toDomain(carModel)), includeDocuments = false).first()
    }

    private suspend fun enrichListings(listings: List<CarListing>, includeDocuments: Boolean): List<CarListing> {
        if (listings.isEmpty()) return listings
        val ids = listings.mapNotNull { it.listingId }
        if (ids.isEmpty()) return listings

        val imagesByCarId = carImageService.findCarIdIn(ids).toList()
            .map { it.toDomain() }
            .groupBy { it.carId }

        val documentsByCarId = if (includeDocuments) {
            carDocumentService.findByCarIdIn(ids).groupBy { it.carId }
        } else {
            emptyMap()
        }

        return listings.map { listing ->
            listing.copy(
                images = imagesByCarId[listing.listingId].orEmpty(),
                documents = if (includeDocuments) documentsByCarId[listing.listingId].orEmpty() else null,
            )
        }
    }

    private fun validateListingRequest(request: CarListingRequest) {
        when (request.listingType) {
            ListingType.SALE -> {
                if (request.price.isNullOrBlank()) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Le prix de vente est obligatoire.")
                }
            }
            ListingType.RENT -> {
                if (request.rentPricePerDay.isNullOrBlank()) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Le prix de location journalier est obligatoire.")
                }
            }
            ListingType.EXCHANGE -> {
                if (request.exchangeDescription.isNullOrBlank()) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Décrivez la pièce ou la voiture souhaitée en échange.")
                }
            }
        }
        if (request.condition == ItemCondition.USED && request.mileageKm <= 0) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Indiquez le kilométrage pour un véhicule d'occasion.")
        }
    }
}
