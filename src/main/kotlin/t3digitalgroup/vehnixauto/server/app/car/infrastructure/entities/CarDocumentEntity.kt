package t3digitalgroup.vehnixauto.server.app.car.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("car_documents")
class CarDocumentEntity(
    @Id
    @Column("id")
    val id: Long? = null,
    @Column("car_id")
    var carId: Long? = null,
    @Column("document_id")
    var documentId: Long,
    @Column("created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),
)
