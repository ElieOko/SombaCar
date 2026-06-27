package initializer.backend.spring.app.notification.infrastructure.repositories

import initializer.backend.spring.app.notification.infrastructure.entities.NotificationEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface NotificationRepository : CoroutineCrudRepository<NotificationEntity, Long> {
    @Query("SELECT * FROM notifications WHERE user_id = :userId AND is_active = true")
    suspend fun getAllByUser(userId: Long): Flow<NotificationEntity?>
}
