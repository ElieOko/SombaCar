package t3digitalgroup.vehnixauto.server.app.message.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.*
import t3digitalgroup.vehnixauto.server.app.message.domain.models.SupportThreadStatus
import java.time.*

@Table(name = "support_threads")
class SupportThreadEntity(
    @Id
    @Column("id")
    val threadId: Long? = null,
    @Column("user_id")
    val userId: Long,
    @Column("subject")
    val subject: String,
    @Column("part_sought")
    val partSought: String,
    @Column("part_reference")
    val partReference: String? = null,
    @Column("compatible_brand")
    val compatibleBrand: String? = null,
    @Column("compatible_model")
    val compatibleModel: String? = null,
    @Column("status")
    var status: String = SupportThreadStatus.OPEN.name,
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column("updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
