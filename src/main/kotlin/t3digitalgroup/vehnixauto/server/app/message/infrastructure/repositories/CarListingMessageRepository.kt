package t3digitalgroup.vehnixauto.server.app.message.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.entities.CarListingMessageEntity

interface CarListingMessageRepository : CoroutineCrudRepository<CarListingMessageEntity, Long> {
    @Query("SELECT * FROM car_listing_messages WHERE thread_id = :threadId ORDER BY sent_at ASC")
    suspend fun findByThreadId(threadId: Long): Flow<CarListingMessageEntity>
}
