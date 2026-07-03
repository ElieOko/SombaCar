package t3digitalgroup.vehnixauto.server.app.message.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.*
import t3digitalgroup.vehnixauto.server.app.message.domain.models.MessageSenderType
import java.time.LocalDateTime

@Table(name = "support_messages")
class MessageEntity(
    @Id
    @Column("id")
    val messageId: Long? = null,
    @Column("thread_id")
    val threadId: Long,
    @Column("sender_type")
    val senderType: String = MessageSenderType.USER.name,
    @Column("sender_id")
    val senderId: Long? = null,
    @Column("content")
    val content: String,
    @Column("is_read")
    var isRead: Boolean = false,
    @Column("sent_at")
    val sentAt: LocalDateTime = LocalDateTime.now()
)
