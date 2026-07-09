package t3digitalgroup.vehnixauto.server.app.car.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("documents")
class DocumentEntity(
    @Id
    @Column("id")
    val id: Long? = null,
    @Column("name")
    val name: String,
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
