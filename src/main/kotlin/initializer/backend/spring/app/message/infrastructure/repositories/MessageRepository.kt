package initializer.backend.spring.app.message.infrastructure.repositories

import initializer.backend.spring.app.message.infrastructure.entities.MessageEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface MessageRepository : CoroutineCrudRepository<MessageEntity, Long> {
    @Query("SELECT * FROM support_messages WHERE thread_id = :threadId ORDER BY sent_at ASC")
    suspend fun findByThreadId(threadId: Long): Flow<MessageEntity>
}
