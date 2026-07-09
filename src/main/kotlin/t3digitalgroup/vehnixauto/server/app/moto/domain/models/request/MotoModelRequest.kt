package t3digitalgroup.vehnixauto.server.app.moto.domain.models.request

import jakarta.validation.constraints.NotBlank

data class MotoModelRequest(
    @NotBlank
    val brand: String,
    @NotBlank
    val model: String,
    val generation: String? = null,
    val bodyType: String? = null,
)
