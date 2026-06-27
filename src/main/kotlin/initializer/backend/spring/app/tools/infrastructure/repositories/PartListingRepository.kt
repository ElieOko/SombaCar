package initializer.backend.spring.app.tools.infrastructure.repositories

import initializer.backend.spring.app.tools.infrastructure.entities.PartListingEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PartListingRepository : CoroutineCrudRepository<PartListingEntity, Long> {
    @Query("SELECT * FROM part_listings WHERE user_id = :userId ORDER BY created_at DESC")
    suspend fun findByUserId(userId: Long): Flow<PartListingEntity>

    @Query("SELECT * FROM part_listings WHERE listing_type = :listingType AND status = 'ACTIVE' ORDER BY created_at DESC")
    suspend fun findActiveByListingType(listingType: String): Flow<PartListingEntity>

    @Query(
        """
        SELECT * FROM part_listings
        WHERE status = 'ACTIVE'
          AND (
            LOWER(name) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(part_reference) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(compatible_brand) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(compatible_model) LIKE LOWER(CONCAT('%', :query, '%'))
          )
        ORDER BY created_at DESC
        """
    )
    suspend fun searchActive(query: String): Flow<PartListingEntity>
}
