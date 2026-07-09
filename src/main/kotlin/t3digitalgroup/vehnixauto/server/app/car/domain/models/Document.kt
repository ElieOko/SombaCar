package t3digitalgroup.vehnixauto.server.app.car.domain.models

import java.time.LocalDateTime

data class Document(
    val documentId: Long? = null,
    val name: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
