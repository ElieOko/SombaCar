package t3digitalgroup.vehnixauto.server.app.message.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "car_listing_message_attachments")
class CarListingMessageAttachmentEntity(
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

@Table(name = "support_message_attachments")
class SupportMessageAttachmentEntity(
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
