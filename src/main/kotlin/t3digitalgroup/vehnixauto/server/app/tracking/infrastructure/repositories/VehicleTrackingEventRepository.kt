package t3digitalgroup.vehnixauto.server.app.tracking.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.tracking.infrastructure.entities.VehicleTrackingEventEntity

interface VehicleTrackingEventRepository : CoroutineCrudRepository<VehicleTrackingEventEntity, Long> {
    @Query(
        """
        SELECT * FROM vehicle_tracking_events
        WHERE vehicle_type = :vehicleType AND vehicle_id = :vehicleId
        ORDER BY created_at DESC
        """
    )
    suspend fun findByVehicle(vehicleType: String, vehicleId: Long): Flow<VehicleTrackingEventEntity>

    @Query(
        """
        SELECT * FROM vehicle_tracking_events
        WHERE vehicle_type = :vehicleType AND vehicle_id = :vehicleId AND is_active = TRUE
        ORDER BY created_at DESC
        LIMIT 1
        """
    )
    suspend fun findLatestActive(vehicleType: String, vehicleId: Long): VehicleTrackingEventEntity?

    @Query(
        """
        SELECT * FROM vehicle_tracking_events
        WHERE rental_request_id = :rentalRequestId
        ORDER BY created_at DESC
        """
    )
    suspend fun findByRentalRequestId(rentalRequestId: Long): Flow<VehicleTrackingEventEntity>
}
