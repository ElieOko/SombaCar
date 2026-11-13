package t3digitalgroup.vehnixauto.server.app.tracking.domain.models

import jakarta.validation.constraints.NotBlank
import t3digitalgroup.vehnixauto.server.utils.TrackingTag
import t3digitalgroup.vehnixauto.server.utils.VehicleKind
import java.time.LocalDateTime

data class TrackingCarDTO(
    val id: Long? = null,
    val userId: Long? = null,
    val carId: Long? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val tag: String? = "Depart",
    val isActive: Boolean = true,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
)

data class TrackingMotoDTO(
    val id: Long? = null,
    val userId: Long? = null,
    val motoId: Long? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val tag: String? = "Depart",
    val isActive: Boolean = true,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
)

data class VehicleTrackingEvent(
    val id: Long? = null,
    val vehicleType: VehicleKind,
    val vehicleId: Long,
    val userId: Long? = null,
    val driverUserId: Long? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val tag: String = TrackingTag.DEPART.name,
    val isActive: Boolean = true,
    val message: String,
    val rentalRequestId: Long? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)

data class TrackingEventRequest(
    val vehicleType: VehicleKind,
    val vehicleId: Long,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val tag: String? = TrackingTag.DEPART.name,
    val isActive: Boolean = true,
    @field:NotBlank
    val message: String,
    val rentalRequestId: Long? = null,
    val driverUserId: Long? = null,
)

data class TrackingKafkaEvent(
    val eventType: String = "VEHICLE_TRACKING",
    val payload: VehicleTrackingEvent,
)
