package t3digitalgroup.vehnixauto.server.app.message.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import t3digitalgroup.vehnixauto.server.app.message.domain.models.CarListingThreadStatus
import java.time.LocalDateTime

@Table(name = "moto_listing_threads")
class MotoListingThreadEntity(
    @Id
    @Column("id")
    val threadId: Long? = null,
    @Column("moto_listing_id")
    val motoListingId: Long,
    @Column("buyer_id")
    val buyerId: Long,
    @Column("seller_id")
    val sellerId: Long,
    @Column("status")
    var status: String = CarListingThreadStatus.OPEN.name,
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column("updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)

@Table(name = "moto_listing_messages")
class MotoListingMessageEntity(
    @Id
    @Column("id")
    val messageId: Long? = null,
    @Column("thread_id")
    val threadId: Long,
    @Column("sender_id")
    val senderId: Long,
    @Column("content")
    val content: String? = null,
    @Column("is_read")
    var isRead: Boolean = false,
    @Column("sent_at")
    val sentAt: LocalDateTime = LocalDateTime.now(),
)

@Table(name = "moto_listing_message_attachments")
class MotoListingMessageAttachmentEntity(
    @Id
    @Column("id")
    val attachmentId: Long? = null,
    @Column("message_id")
    val messageId: Long,
    @Column("name")
    val name: String,
    @Column("path")
    val path: String,
    @Column("mime_type")
    val mimeType: String? = null,
)
