package t3digitalgroup.vehnixauto.server.app.moto.domain.models

import java.time.LocalDateTime

data class MotoModel(
    val motoModelId: Long? = null,
    val brand: String,
    val model: String,
    val generation: String? = null,
    val bodyType: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
