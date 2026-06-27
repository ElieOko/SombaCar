package initializer.backend.spring.app.car.domain.models.request

import jakarta.validation.constraints.NotBlank

data class CarModelRequest(
    @NotBlank
    val brand: String,
    @NotBlank
    val model: String,
    val generation: String? = null,
    val bodyType: String? = null
)
