package t3digitalgroup.vehnixauto.server.app.moto.application.services

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.moto.domain.models.MotoListing
import t3digitalgroup.vehnixauto.server.app.moto.domain.models.request.MotoListingRequest
import t3digitalgroup.vehnixauto.server.app.moto.infrastructure.mapper.toDomain
import t3digitalgroup.vehnixauto.server.app.moto.infrastructure.mapper.toEntity
import t3digitalgroup.vehnixauto.server.app.moto.infrastructure.repositories.MotoListingRepository
import t3digitalgroup.vehnixauto.server.app.moto.infrastructure.repositories.MotoModelRepository
import t3digitalgroup.vehnixauto.server.utils.*
import java.time.LocalDateTime

@Service
@Profile(Mode.DEV)
class MotoListingService(
    private val listingRepository: MotoListingRepository,
    private val motoModelRepository: MotoModelRepository,
    private val motoImageService: MotoImageService,
    private val motoDocumentService: MotoDocumentService,
) {
    suspend fun create(request: MotoListingRequest): MotoListing {
        validateListingRequest(request)
        val motoModel = motoModelRepository.findById(request.motoModelId)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Modèle de moto introuvable.")

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
            rentPricePerDay = request.rentPricePerDay,
            exchangeDescription = request.exchangeDescription,
            description = request.description,
            city = request.city,
            country = request.country,
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

    suspend fun findById(id: Long, includeDocuments: Boolean = false): MotoListing {
        val listing = listingRepository.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Annonce introuvable.")
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

    suspend fun findByListingType(listingType: ListingType, includeDocuments: Boolean = false): List<MotoListing> {
        val listings = listingRepository.findActiveByListingType(listingType.name).map { listing ->
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
                if (request.rentPricePerDay.isNullOrBlank()) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Le prix de location journalier est obligatoire.")
                }
            }
            ListingType.EXCHANGE -> {
                if (request.exchangeDescription.isNullOrBlank()) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Décrivez la moto ou la pièce souhaitée en échange.")
                }
            }
        }
        if (request.condition == ItemCondition.USED && request.mileageKm <= 0) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Indiquez le kilométrage pour une moto d'occasion.")
        }
    }
}
