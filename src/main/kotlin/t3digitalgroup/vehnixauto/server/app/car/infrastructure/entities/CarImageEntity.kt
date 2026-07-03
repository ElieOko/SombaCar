package t3digitalgroup.vehnixauto.server.app.car.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.*

@Table("car_images")
class CarImageEntity(
    @Id
    @Column("id")
    val id: Long? = null,
    @Column("car_id")
    var carId: Long? = null,
    @Column("name")
    var name: String,
    @Column("path")
    var path: String
)