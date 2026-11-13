package t3digitalgroup.vehnixauto.server.app.moto.application.services

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.car.domain.models.request.AssignDriverRequest
import t3digitalgroup.vehnixauto.server.app.moto.domain.models.MotoListing
import t3digitalgroup.vehnixauto.server.app.moto.domain.models.request.MotoListingAdminUpdateRequest
import t3digitalgroup.vehnixauto.server.app.moto.domain.models.request.MotoListingRequest
import t3digitalgroup.vehnixauto.server.app.moto.infrastructure.mapper.toDomain
import t3digitalgroup.vehnixauto.server.app.moto.infrastructure.mapper.toEntity
import t3digitalgroup.vehnixauto.server.app.moto.infrastructure.repositories.MotoListingRepository
import t3digitalgroup.vehnixauto.server.app.moto.infrastructure.repositories.MotoModelRepository
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.repositories.UserRepository
import t3digitalgroup.vehnixauto.server.utils.*
import java.time.LocalDateTime

@Service
@Profile(Mode.DEV)
class MotoListingService(
    private val listingRepository: MotoListingRepository,
    private val motoModelRepository: MotoModelRepository,
    private val motoImageService: MotoImageService,
    private val motoDocumentService: MotoDocumentService,
    private val userRepository: UserRepository,
) {
    suspend fun create(request: MotoListingRequest): MotoListing {
        validateListingRequest(request)
        validateGeoCoordinates(request.latitude, request.longitude)
        val motoModel = motoModelRepository.findById(request.motoModelId)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Modèle de moto introuvable.")
        request.driverUserId?.let { driverId ->
            userRepository.findById(driverId)
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Chauffeur introuvable.")
        }

        val entity = MotoListing(
            userId = request.userId,
            motoModelId = request.motoModelId,
            listingType = request.listingType.name,
            year = request.year,
            isElectric = request.isElectric,
            mileageKm = request.mileageKm,
            condition = request.condition.name,
            plateNumber = request.plateNumber,
            color = request.color,
            engineCc = request.engineCc,
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
                motoDocumentService.linkDocuments(listingId, documentIds)
            }
        }
        return enrichListings(
            listOf(saved.toDomain(motoModel.toDomain())),
            includeDocuments = !request.documentIds.isNullOrEmpty(),
        ).first()
    }

    suspend fun adminUpdate(listingId: Long, request: MotoListingAdminUpdateRequest): MotoListing {
        val entity = listingRepository.findById(listingId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Annonce introuvable.")
        request.motoModelId?.let { modelId ->
            motoModelRepository.findById(modelId)
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Modèle de moto introuvable.")
            entity.motoModelId = modelId
        }
        if (request.clearYear) entity.year = null else request.year?.let { entity.year = it }
        request.isElectric?.let { entity.isElectric = it }
        if (request.clearMileage) entity.mileageKm = null else request.mileageKm?.let { entity.mileageKm = it }
        request.condition?.let { entity.condition = it.name }
        request.plateNumber?.let { entity.plateNumber = it }
        request.color?.let { entity.color = it }
        request.engineCc?.let { entity.engineCc = it }
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
        request.documentIds?.let { ids -> carDocumentOrLink(listingId, ids) }
        val motoModel = motoModelRepository.findById(saved.motoModelId)?.toDomain()
        return enrichListings(listOf(saved.toDomain(motoModel)), includeDocuments = true).first()
    }

    private suspend fun carDocumentOrLink(listingId: Long, ids: List<Long>) {
        motoDocumentService.linkDocuments(listingId, ids)
    }

    suspend fun assignDriver(listingId: Long, request: AssignDriverRequest): MotoListing {
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
        val motoModel = motoModelRepository.findById(saved.motoModelId)?.toDomain()
        return enrichListings(listOf(saved.toDomain(motoModel)), includeDocuments = false).first()
    }

    suspend fun markSold(listingId: Long): MotoListing = updateStatus(listingId, ListingStatus.SOLD)

    suspend fun markNegotiating(listingId: Long): MotoListing = updateStatus(listingId, ListingStatus.NEGOTIATING)

    suspend fun findById(
        id: Long,
        includeDocuments: Boolean = false,
        requireActive: Boolean = false,
    ): MotoListing {
        val listing = listingRepository.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Annonce introuvable.")
        if (requireActive) {
            ensurePubliclyVisible(listing.status)
        }
        val motoModel = motoModelRepository.findById(listing.motoModelId)?.toDomain()
        return enrichListings(listOf(listing.toDomain(motoModel)), includeDocuments).first()
    }

    suspend fun findByUserId(userId: Long, includeDocuments: Boolean = false): List<MotoListing> {
        val listings = listingRepository.findByUserId(userId).map { listing ->
            val motoModel = motoModelRepository.findById(listing.motoModelId)?.toDomain()
            listing.toDomain(motoModel)
        }.toList()
        return enrichListings(listings, includeDocuments)
    }

    suspend fun findByListingType(
        listingType: ListingType,
        includeDocuments: Boolean = false,
        status: ListingStatus? = null,
        includeSoldHistory: Boolean = false,
    ): List<MotoListing> {
        val flow = when {
            status != null -> listingRepository.findByListingTypeAndStatus(listingType.name, status.name)
            includeSoldHistory -> listingRepository.findPublicByListingType(listingType.name)
            else -> listingRepository.findActiveByListingType(listingType.name)
        }
        val listings = flow.map { listing ->
            val motoModel = motoModelRepository.findById(listing.motoModelId)?.toDomain()
            listing.toDomain(motoModel)
        }.toList()
        return enrichListings(listings, includeDocuments)
    }

    suspend fun findByMotoModelId(motoModelId: Long, includeDocuments: Boolean = false): List<MotoListing> {
        val listings = listingRepository.findActiveByMotoModelId(motoModelId).map { listing ->
            val motoModel = motoModelRepository.findById(listing.motoModelId)?.toDomain()
            listing.toDomain(motoModel)
        }.toList()
        return enrichListings(listings, includeDocuments)
    }

    suspend fun findByElectricAndCondition(
        isElectric: Boolean,
        condition: ItemCondition,
        includeDocuments: Boolean = false,
    ): List<MotoListing> {
        val listings = listingRepository.findActiveByElectricAndCondition(isElectric, condition.name).map { listing ->
            val motoModel = motoModelRepository.findById(listing.motoModelId)?.toDomain()
            listing.toDomain(motoModel)
        }.toList()
        return enrichListings(listings, includeDocuments)
    }

    suspend fun updateStatus(listingId: Long, status: ListingStatus): MotoListing {
        val entity = listingRepository.findById(listingId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Annonce introuvable.")
        entity.status = status.name
        entity.updatedAt = LocalDateTime.now()
        val saved = listingRepository.save(entity)
        val motoModel = motoModelRepository.findById(saved.motoModelId)?.toDomain()
        return enrichListings(listOf(saved.toDomain(motoModel)), includeDocuments = false).first()
    }

    suspend fun findAllForAdmin(status: ListingStatus? = null, includeDocuments: Boolean = false): List<MotoListing> {
        val flow = if (status != null) {
            listingRepository.findAllByStatus(status.name)
        } else {
            listingRepository.findAllOrdered()
        }
        val listings = flow.map { listing ->
            val motoModel = motoModelRepository.findById(listing.motoModelId)?.toDomain()
            listing.toDomain(motoModel)
        }.toList()
        return enrichListings(listings, includeDocuments)
    }

    suspend fun updateCoordinates(listingId: Long, request: GeoCoordinatesRequest): MotoListing {
        val entity = listingRepository.findById(listingId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Annonce introuvable.")
        entity.latitude = request.latitude
        entity.longitude = request.longitude
        entity.updatedAt = LocalDateTime.now()
        val saved = listingRepository.save(entity)
        val motoModel = motoModelRepository.findById(saved.motoModelId)?.toDomain()
        return enrichListings(listOf(saved.toDomain(motoModel)), includeDocuments = false).first()
    }

    private suspend fun enrichListings(listings: List<MotoListing>, includeDocuments: Boolean): List<MotoListing> {
        if (listings.isEmpty()) return listings
        val ids = listings.mapNotNull { it.listingId }
        if (ids.isEmpty()) return listings

        val imagesByMotoId = motoImageService.findByMotoIdIn(ids).groupBy { it.motoId }
        val documentsByMotoId = if (includeDocuments) {
            motoDocumentService.findByMotoIdIn(ids).groupBy { it.motoId }
        } else {
            emptyMap()
        }

        return listings.map { listing ->
            listing.copy(
                images = imagesByMotoId[listing.listingId].orEmpty(),
                documents = if (includeDocuments) documentsByMotoId[listing.listingId].orEmpty() else null,
            )
        }
    }

    private fun validateListingRequest(request: MotoListingRequest) {
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
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Décrivez la moto ou la pièce souhaitée en échange.")
                }
            }
        }
    }
}
