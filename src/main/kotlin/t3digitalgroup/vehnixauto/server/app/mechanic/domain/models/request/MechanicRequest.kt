package t3digitalgroup.vehnixauto.server.app.mechanic.domain.models.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class MechanicRequest(
    val garageId: Long? = null,
    @NotBlank
    val fullName: String,
    @NotBlank
    val phone: String,
    val city: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isNightAvailable: Boolean = false,
)

data class MechanicContactRequestBody(
    @NotNull
    val mechanicId: Long,
    val message: String? = null,
)
