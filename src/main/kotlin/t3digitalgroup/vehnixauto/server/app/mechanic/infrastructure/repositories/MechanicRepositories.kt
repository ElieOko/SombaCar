package t3digitalgroup.vehnixauto.server.app.mechanic.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.mechanic.infrastructure.entities.MechanicContactEntity
import t3digitalgroup.vehnixauto.server.app.mechanic.infrastructure.entities.MechanicEntity

interface MechanicRepository : CoroutineCrudRepository<MechanicEntity, Long> {
    @Query(
        """
        SELECT * FROM mechanics
        WHERE is_active = TRUE AND is_night_available = TRUE
        ORDER BY full_name ASC
        """
    )
    suspend fun findNightAvailable(): Flow<MechanicEntity>

    @Query(
        """
        SELECT * FROM mechanics
        WHERE is_active = TRUE
        ORDER BY full_name ASC
        """
    )
    suspend fun findAllActive(): Flow<MechanicEntity>
}

interface MechanicContactRepository : CoroutineCrudRepository<MechanicContactEntity, Long> {
    @Query(
        """
        SELECT * FROM mechanic_contact_requests
        WHERE user_id = :userId
        ORDER BY created_at DESC
        """
    )
    suspend fun findByUserId(userId: Long): Flow<MechanicContactEntity>
}
