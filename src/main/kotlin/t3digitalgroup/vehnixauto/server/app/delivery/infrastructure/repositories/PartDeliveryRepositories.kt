package t3digitalgroup.vehnixauto.server.app.delivery.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.delivery.infrastructure.entities.PartDeliveryEntity
import t3digitalgroup.vehnixauto.server.app.delivery.infrastructure.entities.PartDeliveryTrackingEventEntity

interface PartDeliveryRepository : CoroutineCrudRepository<PartDeliveryEntity, Long> {
    @Query("SELECT * FROM part_deliveries WHERE buyer_user_id = :userId ORDER BY created_at DESC")
    suspend fun findByBuyerUserId(userId: Long): Flow<PartDeliveryEntity>

    @Query("SELECT * FROM part_deliveries WHERE status = :status ORDER BY created_at DESC")
    suspend fun findByStatus(status: String): Flow<PartDeliveryEntity>

    @Query("SELECT * FROM part_deliveries ORDER BY created_at DESC")
    suspend fun findAllOrdered(): Flow<PartDeliveryEntity>
}

interface PartDeliveryTrackingRepository : CoroutineCrudRepository<PartDeliveryTrackingEventEntity, Long> {
    @Query(
        """
        SELECT * FROM part_delivery_tracking_events
        WHERE delivery_id = :deliveryId
        ORDER BY created_at DESC
        """
    )
    suspend fun findByDeliveryId(deliveryId: Long): Flow<PartDeliveryTrackingEventEntity>
}
