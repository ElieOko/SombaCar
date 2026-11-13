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
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.repositories.UserRepository
import t3digitalgroup.vehnixauto.server.utils.*
import java.time.LocalDateTime

@Service
@Profile(Mode.DEV)
class CarListingService(
    private val listingRepository: CarListingRepository,
    private val carModelRepository: CarModelRepository,
    private val carImageService: CarImageService,
    private val carDocumentService: CarDocumentService,
    private val userRepository: UserRepository,
) {
    suspend fun create(request: CarListingRequest): CarListing {
        validateListingRequest(request)
        validateGeoCoordinates(request.latitude, request.longitude)
        userRepository.findById(request.userId)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Utilisateur introuvable.")
        val carModel = carModelRepository.findById(request.carModelId)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Modèle de voiture introuvable.")
        request.driverUserId?.let { driverId ->
            userRepository.findById(driverId)
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Chauffeur introuvable.")
        }

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
            devise = request.devise,
            numberVin = request.numberVin,
            rentPricePerDay = request.rentPricePerDay,
            rentPricePerWeek = request.rentPricePerWeek,
            rentPricePerMonth = request.rentPricePerMonth,
            transmission = request.transmission?.name,
            withDriver = request.withDriver || request.driverUserId != null,
            driverUserId = request.driverUserId,
            rentalAvailable = request.rentalAvailable,
            exchangeDescription = request.exchangeDescription,
            description = request.description,
            city = request.city,
            country = request.country,
            latitude = request.latitude,
            longitude = request.longitude,
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

    suspend fun adminUpdate(listingId: Long, request: CarListingAdminUpdateRequest): CarListing {
        val entity = listingRepository.findById(listingId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Annonce introuvable.")
        request.carModelId?.let { modelId ->
            carModelRepository.findById(modelId)
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Modèle de voiture introuvable.")
            entity.carModelId = modelId
        }
        if (request.clearYear) entity.year = null else request.year?.let { entity.year = it }
        request.isElectric?.let { entity.isElectric = it }
        if (request.clearMileage) entity.mileageKm = null else request.mileageKm?.let { entity.mileageKm = it }
        request.condition?.let { entity.condition = it.name }
        request.plateNumber?.let { entity.plateNumber = it }
        request.color?.let { entity.color = it }
        request.seats?.let { entity.seats = it }
        request.price?.let { entity.price = it }
        request.devise?.let { entity.deviseId = it }
        request.numberVin?.let { entity.numberVin = it }
        request.rentPricePerDay?.let { entity.rentPricePerDay = it }
        request.rentPricePerWeek?.let { entity.rentPricePerWeek = it }
        request.rentPricePerMonth?.let { entity.rentPricePerMonth = it }
        request.transmission?.let { entity.transmission = it.name }
        request.withDriver?.let { entity.withDriver = it }
        if (request.clearDriver) {
            entity.driverUserId = null
            entity.withDriver = false
        } else {
            request.driverUserId?.let { driverId ->
                userRepository.findById(driverId)
                    ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Chauffeur introuvable.")
                entity.driverUserId = driverId
                entity.withDriver = true
            }
        }
        request.rentalAvailable?.let { entity.rentalAvailable = it }
        request.exchangeDescription?.let { entity.exchangeDescription = it }
        request.description?.let { entity.description = it }
        request.city?.let { entity.city = it }
        request.country?.let { entity.country = it }
        if (request.latitude != null || request.longitude != null) {
            validateGeoCoordinates(request.latitude ?: entity.latitude, request.longitude ?: entity.longitude)
            request.latitude?.let { entity.latitude = it }
            request.longitude?.let { entity.longitude = it }
        }
        request.status?.let { entity.status = it.name }
        entity.updatedAt = LocalDateTime.now()
        val saved = listingRepository.save(entity)
        request.documentIds?.let { ids ->
            carDocumentService.linkDocuments(listingId, ids)
        }
        val carModel = carModelRepository.findById(saved.carModelId)?.toDomain()
        return enrichListings(listOf(saved.toDomain(carModel)), includeDocuments = true).first()
    }

    suspend fun assignDriver(listingId: Long, request: AssignDriverRequest): CarListing {
        val entity = listingRepository.findById(listingId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Annonce introuvable.")
        if (entity.listingType != ListingType.RENT.name) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Le chauffeur ne s'applique qu'aux annonces de location.")
        }
        if (request.driverUserId == null) {
            entity.driverUserId = null
            entity.withDriver = request.withDriver
        } else {
            userRepository.findById(request.driverUserId)
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Chauffeur introuvable.")
            entity.driverUserId = request.driverUserId
            entity.withDriver = true
        }
        entity.updatedAt = LocalDateTime.now()
        val saved = listingRepository.save(entity)
        val carModel = carModelRepository.findById(saved.carModelId)?.toDomain()
        return enrichListings(listOf(saved.toDomain(carModel)), includeDocuments = false).first()
    }

    suspend fun markSold(listingId: Long): CarListing = updateStatus(listingId, ListingStatus.SOLD)

    suspend fun markNegotiating(listingId: Long): CarListing = updateStatus(listingId, ListingStatus.NEGOTIATING)

    suspend fun findById(
        id: Long,
        includeDocuments: Boolean = false,
        requireActive: Boolean = false,
    ): CarListing {
        val listing = listingRepository.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Annonce introuvable.")
        if (requireActive) {
            ensurePubliclyVisible(listing.status)
        }
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

    suspend fun findByListingType(
        listingType: ListingType,
        includeDocuments: Boolean = false,
        status: ListingStatus? = null,
        includeSoldHistory: Boolean = false,
    ): List<CarListing> {
        val flow = when {
            status != null -> listingRepository.findByListingTypeAndStatus(listingType.name, status.name)
            includeSoldHistory -> listingRepository.findPublicByListingType(listingType.name)
            else -> listingRepository.findActiveByListingType(listingType.name)
        }
        val listings = flow.map { listing ->
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

    suspend fun findAllForAdmin(status: ListingStatus? = null, includeDocuments: Boolean = false): List<CarListing> {
        val flow = if (status != null) {
            listingRepository.findAllByStatus(status.name)
        } else {
            listingRepository.findAllOrdered()
        }
        val listings = flow.map { listing ->
            val carModel = carModelRepository.findById(listing.carModelId)?.toDomain()
            listing.toDomain(carModel)
        }.toList()
        return enrichListings(listings, includeDocuments)
    }

    suspend fun updateCoordinates(listingId: Long, request: GeoCoordinatesRequest): CarListing {
        val entity = listingRepository.findById(listingId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Annonce introuvable.")
        entity.latitude = request.latitude
        entity.longitude = request.longitude
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

        val userIds = listings.map { it.userId }.distinct()
        val fullNameByUserId = userRepository.findByUserIdIn(userIds).toList()
            .associate { it.userId!! to it.fullName }

        return listings.map { listing ->
            listing.copy(
                userFullName = fullNameByUserId[listing.userId],
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
                if (request.rentPricePerDay.isNullOrBlank() &&
                    request.rentPricePerWeek.isNullOrBlank() &&
                    request.rentPricePerMonth.isNullOrBlank()
                ) {
                    throw ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Au moins un tarif de location (jour, semaine ou mois) est obligatoire.",
                    )
                }
            }
            ListingType.EXCHANGE -> {
                if (request.exchangeDescription.isNullOrBlank()) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Décrivez la pièce ou la voiture souhaitée en échange.")
                }
            }
        }
        // Année et kilométrage sont facultatifs (CDC)
    }
}
