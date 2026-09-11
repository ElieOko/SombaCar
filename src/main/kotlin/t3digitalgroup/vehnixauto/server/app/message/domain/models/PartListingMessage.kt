package t3digitalgroup.vehnixauto.server.app.message.domain.models

import java.time.LocalDateTime

data class PartListingThread(
    val threadId: Long? = null,
    val partListingId: Long,
    val buyerId: Long,
    val sellerId: Long,
    val status: String = CarListingThreadStatus.OPEN.name,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)

data class PartListingMessage(
    val messageId: Long? = null,
    val threadId: Long,
    val senderId: Long,
    val content: String? = null,
    val isRead: Boolean = false,
    val sentAt: LocalDateTime = LocalDateTime.now(),
    val attachments: List<MessageAttachment> = emptyList(),
)
