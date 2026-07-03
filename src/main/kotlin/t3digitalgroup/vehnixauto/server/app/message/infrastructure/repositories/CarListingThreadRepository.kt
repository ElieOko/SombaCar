package t3digitalgroup.vehnixauto.server.app.message.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.entities.CarListingThreadEntity

interface CarListingThreadRepository : CoroutineCrudRepository<CarListingThreadEntity, Long> {
    @Query(
        """
        SELECT * FROM car_listing_threads
        WHERE car_listing_id = :carListingId AND buyer_id = :buyerId
        LIMIT 1
        """
    )
    suspend fun findByCarListingIdAndBuyerId(carListingId: Long, buyerId: Long): CarListingThreadEntity?

    @Query(
        """
        SELECT * FROM car_listing_threads
        WHERE buyer_id = :userId OR seller_id = :userId
        ORDER BY updated_at DESC
        """
    )
    suspend fun findByParticipantUserId(userId: Long): Flow<CarListingThreadEntity>

    @Query(
        """
        SELECT * FROM car_listing_threads
        WHERE car_listing_id = :carListingId
        ORDER BY updated_at DESC
        """
    )
    suspend fun findByCarListingId(carListingId: Long): Flow<CarListingThreadEntity>
}
