package t3digitalgroup.vehnixauto.server.app.notification.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.notification.infrastructure.entities.NotificationEntity

interface NotificationRepository : CoroutineCrudRepository<NotificationEntity, Long> {
    @Query("SELECT * FROM notifications WHERE user_id = :userId AND is_active = true")
    suspend fun getAllByUser(userId: Long): Flow<NotificationEntity?>
}
