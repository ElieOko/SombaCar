package t3digitalgroup.vehnixauto.server.app.report.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.report.infrastructure.entities.ListingReportEntity

interface ListingReportRepository : CoroutineCrudRepository<ListingReportEntity, Long> {
    @Query(
        """
        SELECT COUNT(*) FROM listing_reports
        WHERE listing_type = :listingType AND listing_id = :listingId
        """
    )
    suspend fun countByListing(listingType: String, listingId: Long): Long

    @Query(
        """
        SELECT * FROM listing_reports
        WHERE listing_type = :listingType AND listing_id = :listingId
        ORDER BY created_at DESC
        """
    )
    suspend fun findByListing(listingType: String, listingId: Long): Flow<ListingReportEntity>

    @Query(
        """
        SELECT * FROM listing_reports
        WHERE reported_by = :userId
        ORDER BY created_at DESC
        """
    )
    suspend fun findByReportedBy(userId: Long): Flow<ListingReportEntity>

    suspend fun existsByReportedByAndListingTypeAndListingId(
        reportedBy: Long,
        listingType: String,
        listingId: Long,
    ): Boolean
}
