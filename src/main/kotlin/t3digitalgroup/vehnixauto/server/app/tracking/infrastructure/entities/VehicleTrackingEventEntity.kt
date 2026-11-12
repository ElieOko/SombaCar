package t3digitalgroup.vehnixauto.server.app.tracking.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("vehicle_tracking_events")
class VehicleTrackingEventEntity(
    @Id
    @Column("id")
    val id: Long? = null,
    @Column("vehicle_type")
    val vehicleType: String,
    @Column("vehicle_id")
    val vehicleId: Long,
    @Column("user_id")
    val userId: Long? = null,
    @Column("driver_user_id")
    val driverUserId: Long? = null,
    @Column("latitude")
    val latitude: Double? = null,
    @Column("longitude")
    val longitude: Double? = null,
    @Column("tag")
    val tag: String = "Depart",
    @Column("is_active")
    var isActive: Boolean = true,
    @Column("message")
    val message: String,
    @Column("rental_request_id")
    val rentalRequestId: Long? = null,
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
