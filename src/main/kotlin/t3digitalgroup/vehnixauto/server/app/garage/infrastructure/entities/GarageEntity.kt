package t3digitalgroup.vehnixauto.server.app.garage.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import t3digitalgroup.vehnixauto.server.utils.GarageType
import java.time.LocalDateTime

@Table(name = "garages")
class GarageEntity(
    @Id
    @Column("id")
    val garageId: Long? = null,
    @Column("user_id")
    val userId: Long,
    @Column("name")
    var name: String,
    @Column("description")
    var description: String? = null,
    @Column("address")
    var address: String? = null,
    @Column("city")
    var city: String? = null,
    @Column("country")
    var country: String = "Democratic Republic of the Congo",
    @Column("phone")
    var phone: String? = null,
    @Column("latitude")
    var latitude: Double? = null,
    @Column("longitude")
    var longitude: Double? = null,
    @Column("garage_type")
    var garageType: String = GarageType.CAR.name,
    @Column("is_active")
    var isActive: Boolean = true,
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column("updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)
