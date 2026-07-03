package t3digitalgroup.vehnixauto.server.app.message.domain.models

import java.time.LocalDateTime

enum class CarListingThreadStatus {
    OPEN,
    CLOSED,
}

data class CarListingThread(
    val threadId: Long? = null,
    val carListingId: Long,
    val buyerId: Long,
    val sellerId: Long,
    val status: String = CarListingThreadStatus.OPEN.name,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)

data class CarListingMessage(
    val messageId: Long? = null,
    val threadId: Long,
    val senderId: Long,
    val content: String,
    val isRead: Boolean = false,
    val sentAt: LocalDateTime = LocalDateTime.now(),
)
