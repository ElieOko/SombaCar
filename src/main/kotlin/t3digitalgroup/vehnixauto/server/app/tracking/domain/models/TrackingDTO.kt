package t3digitalgroup.vehnixauto.server.app.tracking.domain.models

import java.time.LocalDateTime

data class TrackingCarDTO(
    val id: Long? = null,
    val userId: Long? = null,
    val carId: Long? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val tag : String? = "Depart",
    val isActive: Boolean = true,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
//    var user : UserDto? = null
)
