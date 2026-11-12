package t3digitalgroup.vehnixauto.server.app.rental.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.rental.infrastructure.entities.RentalRequestEntity

interface RentalRequestRepository : CoroutineCrudRepository<RentalRequestEntity, Long> {
    @Query("SELECT * FROM rental_requests WHERE client_user_id = :userId ORDER BY created_at DESC")
    suspend fun findByClientUserId(userId: Long): Flow<RentalRequestEntity>

    @Query("SELECT * FROM rental_requests WHERE status = :status ORDER BY created_at DESC")
    suspend fun findByStatus(status: String): Flow<RentalRequestEntity>

    @Query(
        """
        SELECT * FROM rental_requests
        WHERE vehicle_type = :vehicleType AND vehicle_listing_id = :listingId
        ORDER BY created_at DESC
        """
    )
    suspend fun findByVehicle(vehicleType: String, listingId: Long): Flow<RentalRequestEntity>

    @Query("SELECT * FROM rental_requests ORDER BY created_at DESC")
    suspend fun findAllOrdered(): Flow<RentalRequestEntity>
}
