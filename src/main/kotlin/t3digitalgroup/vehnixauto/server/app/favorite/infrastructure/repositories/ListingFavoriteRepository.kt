package t3digitalgroup.vehnixauto.server.app.favorite.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.favorite.infrastructure.entities.ListingFavoriteEntity

interface ListingFavoriteRepository : CoroutineCrudRepository<ListingFavoriteEntity, Long> {
    @Query(
        """
        SELECT * FROM listing_favorites
        WHERE user_id = :userId
        ORDER BY created_at DESC
        """
    )
    suspend fun findByUserId(userId: Long): Flow<ListingFavoriteEntity>

    @Query(
        """
        SELECT * FROM listing_favorites
        WHERE user_id = :userId AND listing_type = :listingType
        ORDER BY created_at DESC
        """
    )
    suspend fun findByUserIdAndListingType(userId: Long, listingType: String): Flow<ListingFavoriteEntity>

    suspend fun findByUserIdAndListingTypeAndListingId(
        userId: Long,
        listingType: String,
        listingId: Long,
    ): ListingFavoriteEntity?

    suspend fun deleteByUserIdAndListingTypeAndListingId(
        userId: Long,
        listingType: String,
        listingId: Long,
    ): Long
}
