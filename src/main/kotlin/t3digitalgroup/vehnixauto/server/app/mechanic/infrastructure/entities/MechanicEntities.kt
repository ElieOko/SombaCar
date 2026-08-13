package t3digitalgroup.vehnixauto.server.app.mechanic.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "mechanics")
class MechanicEntity(
    @Id
    @Column("id")
    val mechanicId: Long? = null,
    @Column("garage_id")
    val garageId: Long? = null,
    @Column("full_name")
    var fullName: String,
    @Column("phone")
    var phone: String,
    @Column("city")
    var city: String? = null,
    @Column("latitude")
    var latitude: Double? = null,
    @Column("longitude")
    var longitude: Double? = null,
    @Column("is_night_available")
    var isNightAvailable: Boolean = false,
    @Column("is_active")
    var isActive: Boolean = true,
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column("updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)

@Table(name = "mechanic_contact_requests")
class MechanicContactEntity(
    @Id
    @Column("id")
    val contactId: Long? = null,
    @Column("user_id")
    val userId: Long,
    @Column("mechanic_id")
    val mechanicId: Long,
    @Column("message")
    val message: String? = null,
    @Column("status")
    var status: String,
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
