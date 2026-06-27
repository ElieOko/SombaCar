package initializer.backend.spring.app.message.infrastructure.repositories

import initializer.backend.spring.app.message.infrastructure.entities.SupportThreadEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface SupportThreadRepository : CoroutineCrudRepository<SupportThreadEntity, Long> {
    @Query("SELECT * FROM support_threads WHERE user_id = :userId ORDER BY updated_at DESC")
    suspend fun findByUserId(userId: Long): Flow<SupportThreadEntity>

    @Query("SELECT * FROM support_threads WHERE status = :status ORDER BY updated_at DESC")
    suspend fun findByStatus(status: String): Flow<SupportThreadEntity>
}
