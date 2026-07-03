package t3digitalgroup.vehnixauto.server.app.car.domain.models

import java.time.LocalDateTime

data class CarModel(
    val carModelId: Long? = null,
    val brand: String,
    val model: String,
    val generation: String? = null,
    val bodyType: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
