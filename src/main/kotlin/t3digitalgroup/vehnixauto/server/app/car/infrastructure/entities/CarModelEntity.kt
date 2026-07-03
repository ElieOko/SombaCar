package t3digitalgroup.vehnixauto.server.app.car.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.*
import java.time.*

@Table(name = "car_models")
class CarModelEntity(
    @Id
    @Column("id")
    val carModelId: Long? = null,
    @Column("brand")
    val brand: String,
    @Column("model")
    val model: String,
    @Column("generation")
    val generation: String? = null,
    @Column("body_type")
    val bodyType: String? = null,
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
