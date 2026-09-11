package t3digitalgroup.vehnixauto.server.app.garage.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("garage_images")
class GarageImageEntity(
    @Id
    @Column("id")
    val id: Long? = null,
    @Column("garage_id")
    var garageId: Long? = null,
    @Column("name")
    var name: String,
    @Column("path")
    var path: String,
)
