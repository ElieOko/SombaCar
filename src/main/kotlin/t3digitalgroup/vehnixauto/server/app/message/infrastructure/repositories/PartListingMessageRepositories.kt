package t3digitalgroup.vehnixauto.server.app.message.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.entities.PartListingMessageAttachmentEntity
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.entities.PartListingMessageEntity
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.entities.PartListingThreadEntity

interface PartListingThreadRepository : CoroutineCrudRepository<PartListingThreadEntity, Long> {
    @Query(
        """
        SELECT * FROM part_listing_threads
        WHERE part_listing_id = :partListingId AND buyer_id = :buyerId
        LIMIT 1
        """
    )
    suspend fun findByPartListingIdAndBuyerId(partListingId: Long, buyerId: Long): PartListingThreadEntity?

    @Query(
        """
        SELECT * FROM part_listing_threads
        WHERE buyer_id = :userId OR seller_id = :userId
        ORDER BY updated_at DESC
        """
    )
    suspend fun findByParticipantUserId(userId: Long): Flow<PartListingThreadEntity>

    @Query(
        """
        SELECT * FROM part_listing_threads
        WHERE part_listing_id = :partListingId
        ORDER BY updated_at DESC
        """
    )
    suspend fun findByPartListingId(partListingId: Long): Flow<PartListingThreadEntity>
}

interface PartListingMessageRepository : CoroutineCrudRepository<PartListingMessageEntity, Long> {
    @Query("SELECT * FROM part_listing_messages WHERE thread_id = :threadId ORDER BY sent_at ASC")
    suspend fun findByThreadId(threadId: Long): Flow<PartListingMessageEntity>
}

interface PartListingMessageAttachmentRepository : CoroutineCrudRepository<PartListingMessageAttachmentEntity, Long> {
    @Query("SELECT * FROM part_listing_message_attachments WHERE message_id IN (:messageIds)")
    suspend fun findByMessageIdIn(messageIds: List<Long>): Flow<PartListingMessageAttachmentEntity>
}
