package t3digitalgroup.vehnixauto.server.app.message.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.*
import java.time.*

@Table(name = "car_listing_messages")
class CarListingMessageEntity(
    @Id
    @Column("id")
    val messageId: Long? = null,
    @Column("thread_id")
    val threadId: Long,
    @Column("sender_id")
    val senderId: Long,
    @Column("content")
    val content: String,
    @Column("is_read")
    var isRead: Boolean = false,
    @Column("sent_at")
    val sentAt: LocalDateTime = LocalDateTime.now(),
)
