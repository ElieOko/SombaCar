package initializer.backend.spring.app.car.infrastructure.repositories

import initializer.backend.spring.app.car.infrastructure.entities.CarListingEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CarListingRepository : CoroutineCrudRepository<CarListingEntity, Long> {
    @Query("SELECT * FROM car_listings WHERE user_id = :userId ORDER BY created_at DESC")
    suspend fun findByUserId(userId: Long): Flow<CarListingEntity>

    @Query("SELECT * FROM car_listings WHERE listing_type = :listingType AND status = 'ACTIVE' ORDER BY created_at DESC")
    suspend fun findActiveByListingType(listingType: String): Flow<CarListingEntity>

    @Query("SELECT * FROM car_listings WHERE car_model_id = :carModelId AND status = 'ACTIVE' ORDER BY created_at DESC")
    suspend fun findActiveByCarModelId(carModelId: Long): Flow<CarListingEntity>

    @Query(
        """
        SELECT * FROM car_listings
        WHERE status = 'ACTIVE'
          AND is_electric = :isElectric
          AND condition = :condition
        ORDER BY created_at DESC
        """
    )
    suspend fun findActiveByElectricAndCondition(isElectric: Boolean, condition: String): Flow<CarListingEntity>
}
