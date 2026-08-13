package t3digitalgroup.vehnixauto.server.app.garage.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.garage.infrastructure.entities.GarageEntity

interface GarageRepository : CoroutineCrudRepository<GarageEntity, Long> {
    @Query(
        """
        SELECT * FROM garages
        WHERE user_id = :userId AND is_active = TRUE
        ORDER BY created_at DESC
        """
    )
    suspend fun findActiveByUserId(userId: Long): Flow<GarageEntity>

    @Query(
        """
        SELECT * FROM garages
        WHERE user_id = :userId
        ORDER BY created_at DESC
        """
    )
    suspend fun findAllByUserId(userId: Long): Flow<GarageEntity>

    @Query(
        """
        SELECT * FROM garages
        WHERE is_active = TRUE
        ORDER BY created_at DESC
        """
    )
    suspend fun findAllActive(): Flow<GarageEntity>

    @Query(
        """
        SELECT * FROM garages
        WHERE garage_type = :garageType AND is_active = TRUE
        ORDER BY created_at DESC
        """
    )
    suspend fun findActiveByType(garageType: String): Flow<GarageEntity>
}
