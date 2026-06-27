package initializer.backend.spring.app.message.infrastructure.entities

import initializer.backend.spring.app.message.domain.models.SupportThreadStatus
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

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
