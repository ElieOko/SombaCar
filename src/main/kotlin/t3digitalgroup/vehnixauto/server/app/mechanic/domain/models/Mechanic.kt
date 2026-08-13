package t3digitalgroup.vehnixauto.server.app.mechanic.domain.models

import java.time.LocalDateTime

data class Mechanic(
    val mechanicId: Long? = null,
    val garageId: Long? = null,
    val fullName: String,
    val phone: String,
    val city: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isNightAvailable: Boolean = false,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)

data class MechanicContactRequest(
    val contactId: Long? = null,
    val userId: Long,
    val mechanicId: Long,
    val message: String? = null,
    val status: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
