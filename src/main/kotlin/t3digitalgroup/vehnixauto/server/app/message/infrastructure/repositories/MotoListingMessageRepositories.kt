package t3digitalgroup.vehnixauto.server.app.message.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.entities.MotoListingMessageAttachmentEntity
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.entities.MotoListingMessageEntity
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.entities.MotoListingThreadEntity

interface MotoListingThreadRepository : CoroutineCrudRepository<MotoListingThreadEntity, Long> {
    @Query(
        """
        SELECT * FROM moto_listing_threads
        WHERE moto_listing_id = :motoListingId AND buyer_id = :buyerId
        LIMIT 1
        """
    )
    suspend fun findByMotoListingIdAndBuyerId(motoListingId: Long, buyerId: Long): MotoListingThreadEntity?

    @Query(
        """
        SELECT * FROM moto_listing_threads
        WHERE buyer_id = :userId OR seller_id = :userId
        ORDER BY updated_at DESC
        """
    )
    suspend fun findByParticipantUserId(userId: Long): Flow<MotoListingThreadEntity>

    @Query(
        """
        SELECT * FROM moto_listing_threads
        WHERE moto_listing_id = :motoListingId
        ORDER BY updated_at DESC
        """
    )
    suspend fun findByMotoListingId(motoListingId: Long): Flow<MotoListingThreadEntity>
}

interface MotoListingMessageRepository : CoroutineCrudRepository<MotoListingMessageEntity, Long> {
    @Query("SELECT * FROM moto_listing_messages WHERE thread_id = :threadId ORDER BY sent_at ASC")
    suspend fun findByThreadId(threadId: Long): Flow<MotoListingMessageEntity>
}

interface MotoListingMessageAttachmentRepository : CoroutineCrudRepository<MotoListingMessageAttachmentEntity, Long> {
    @Query("SELECT * FROM moto_listing_message_attachments WHERE message_id IN (:messageIds)")
    suspend fun findByMessageIdIn(messageIds: List<Long>): Flow<MotoListingMessageAttachmentEntity>
}
