package t3digitalgroup.vehnixauto.server.app.favorite.application.services

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.car.application.services.CarListingService
import t3digitalgroup.vehnixauto.server.app.favorite.domain.models.ListingFavorite
import t3digitalgroup.vehnixauto.server.app.favorite.domain.models.request.ListingFavoriteRequest
import t3digitalgroup.vehnixauto.server.app.favorite.infrastructure.mapper.toDomain
import t3digitalgroup.vehnixauto.server.app.favorite.infrastructure.mapper.toEntity
import t3digitalgroup.vehnixauto.server.app.favorite.infrastructure.repositories.ListingFavoriteRepository
import t3digitalgroup.vehnixauto.server.app.moto.application.services.MotoListingService
import t3digitalgroup.vehnixauto.server.app.tools.application.services.PartListingService
import t3digitalgroup.vehnixauto.server.utils.Mode
import t3digitalgroup.vehnixauto.server.utils.OfferType

@Service
@Profile(Mode.DEV)
class ListingFavoriteService(
    private val repository: ListingFavoriteRepository,
    private val carListingService: CarListingService,
    private val partListingService: PartListingService,
    private val motoListingService: MotoListingService,
) {
    suspend fun add(userId: Long, request: ListingFavoriteRequest): ListingFavorite {
        ensureListingExists(request.listingType, request.listingId)

        val listingType = request.listingType.name
        if (repository.findByUserIdAndListingTypeAndListingId(userId, listingType, request.listingId) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Cette annonce est déjà en favoris.")
        }

        return repository.save(
            ListingFavorite(
                userId = userId,
                listingType = listingType,
                listingId = request.listingId,
            ).toEntity()
        ).toDomain()
    }

    suspend fun remove(userId: Long, listingType: OfferType, listingId: Long) {
        val deleted = repository.deleteByUserIdAndListingTypeAndListingId(
            userId,
            listingType.name,
            listingId,
        )
        if (deleted == 0L) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Favori introuvable.")
        }
    }

    suspend fun findByUserId(userId: Long): List<ListingFavorite> =
        repository.findByUserId(userId).map { it.toDomain() }.toList()

    suspend fun findByUserIdAndType(userId: Long, listingType: OfferType): List<ListingFavorite> =
        repository.findByUserIdAndListingType(userId, listingType.name).map { it.toDomain() }.toList()

    suspend fun isFavorite(userId: Long, listingType: OfferType, listingId: Long): Boolean =
        repository.findByUserIdAndListingTypeAndListingId(userId, listingType.name, listingId) != null

    private suspend fun ensureListingExists(listingType: OfferType, listingId: Long) {
        when (listingType) {
            OfferType.CAR -> carListingService.findById(listingId)
            OfferType.PART -> partListingService.findById(listingId)
            OfferType.MOTO -> motoListingService.findById(listingId)
        }
    }
}
