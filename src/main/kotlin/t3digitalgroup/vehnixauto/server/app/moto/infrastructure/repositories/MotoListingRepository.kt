package t3digitalgroup.vehnixauto.server.app.moto.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.moto.infrastructure.entities.MotoListingEntity

interface MotoListingRepository : CoroutineCrudRepository<MotoListingEntity, Long> {
    @Query("SELECT * FROM moto_listings WHERE user_id = :userId ORDER BY created_at DESC")
    suspend fun findByUserId(userId: Long): Flow<MotoListingEntity>

    @Query("SELECT * FROM moto_listings WHERE listing_type = :listingType AND status = 'ACTIVE' ORDER BY created_at DESC")
    suspend fun findActiveByListingType(listingType: String): Flow<MotoListingEntity>

    @Query("SELECT * FROM moto_listings WHERE moto_model_id = :motoModelId AND status = 'ACTIVE' ORDER BY created_at DESC")
    suspend fun findActiveByMotoModelId(motoModelId: Long): Flow<MotoListingEntity>

    @Query(
        """
        SELECT * FROM moto_listings
        WHERE status = 'ACTIVE'
          AND is_electric = :isElectric
          AND condition = :condition
        ORDER BY created_at DESC
        """
    )
    suspend fun findActiveByElectricAndCondition(isElectric: Boolean, condition: String): Flow<MotoListingEntity>
}
