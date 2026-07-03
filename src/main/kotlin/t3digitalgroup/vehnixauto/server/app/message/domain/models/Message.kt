package t3digitalgroup.vehnixauto.server.app.message.domain.models

import java.time.LocalDateTime

enum class SupportThreadStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED
}

enum class MessageSenderType {
    USER,
    PLATFORM
}

data class SupportThread(
    val threadId: Long? = null,
    val userId: Long,
    val subject: String,
    val partSought: String,
    val partReference: String? = null,
    val compatibleBrand: String? = null,
    val compatibleModel: String? = null,
    val status: String = SupportThreadStatus.OPEN.name,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

data class Message(
    val messageId: Long? = null,
    val threadId: Long,
    val senderType: String = MessageSenderType.USER.name,
    val senderId: Long? = null,
    val content: String,
    val isRead: Boolean = false,
    val sentAt: LocalDateTime = LocalDateTime.now()
)
