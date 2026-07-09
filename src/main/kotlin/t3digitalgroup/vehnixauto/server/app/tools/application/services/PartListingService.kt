package t3digitalgroup.vehnixauto.server.app.tools.application.services

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.tools.domain.models.PartListing
import t3digitalgroup.vehnixauto.server.app.tools.domain.models.request.PartListingRequest
import t3digitalgroup.vehnixauto.server.app.tools.infrastructure.mapper.toDomain
import t3digitalgroup.vehnixauto.server.app.tools.infrastructure.mapper.toEntity
import t3digitalgroup.vehnixauto.server.app.tools.infrastructure.repositories.PartListingRepository
import t3digitalgroup.vehnixauto.server.utils.ListingStatus
import t3digitalgroup.vehnixauto.server.utils.ListingType
import t3digitalgroup.vehnixauto.server.utils.Mode
import java.time.LocalDateTime

@Service
@Profile(Mode.DEV)
class PartListingService(
    private val repository: PartListingRepository,
    private val partImageService: PartImageService,
) {
    suspend fun create(request: PartListingRequest): PartListing {
        validateListingRequest(request)
        val entity = PartListing(
            userId = request.userId,
            name = request.name,
            partReference = request.partReference,
            brand = request.brand,
            compatibleBrand = request.compatibleBrand,
            compatibleModel = request.compatibleModel,
            listingType = request.listingType.name,
            condition = request.condition.name,
            price = request.price,
            rentPricePerDay = request.rentPricePerDay,
            exchangeDescription = request.exchangeDescription,
            description = request.description,
            city = request.city,
            country = request.country
        ).toEntity()
        return repository.save(entity).toDomain()
    }

    suspend fun findById(id: Long): PartListing {
        val listing = repository.findById(id)?.toDomain()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Pièce introuvable.")
        return enrichListings(listOf(listing)).first()
    }

    suspend fun findByUserId(userId: Long): List<PartListing> {
        val listings = repository.findByUserId(userId).map { it.toDomain() }.toList()
        return enrichListings(listings)
    }

    suspend fun findByListingType(listingType: ListingType): List<PartListing> {
        val listings = repository.findActiveByListingType(listingType.name).map { it.toDomain() }.toList()
        return enrichListings(listings)
    }

    suspend fun search(query: String): List<PartListing> {
        val listings = repository.searchActive(query).map { it.toDomain() }.toList()
        return enrichListings(listings)
    }

    suspend fun updateStatus(partListingId: Long, status: ListingStatus): PartListing {
        val entity = repository.findById(partListingId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Pièce introuvable.")
        entity.status = status.name
        entity.updatedAt = LocalDateTime.now()
        val saved = repository.save(entity).toDomain()
        return enrichListings(listOf(saved)).first()
    }

    private suspend fun enrichListings(listings: List<PartListing>): List<PartListing> {
        if (listings.isEmpty()) return listings
        val ids = listings.mapNotNull { it.partListingId }
        if (ids.isEmpty()) return listings

        val imagesByPartId = partImageService.findByPartListingIdIn(ids)
            .groupBy { it.partListingId }

        return listings.map { listing ->
            listing.copy(images = imagesByPartId[listing.partListingId].orEmpty())
        }
    }

    private fun validateListingRequest(request: PartListingRequest) {
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
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Décrivez la pièce souhaitée en échange.")
                }
            }
        }
    }
}
