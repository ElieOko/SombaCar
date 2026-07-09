package t3digitalgroup.vehnixauto.server.app.moto.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("moto_documents")
class MotoDocumentEntity(
    @Id
    @Column("id")
    val id: Long? = null,
    @Column("moto_id")
    var motoId: Long? = null,
    @Column("document_id")
    var documentId: Long,
    @Column("created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),
)
