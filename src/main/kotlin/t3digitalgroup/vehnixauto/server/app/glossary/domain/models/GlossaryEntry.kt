package t3digitalgroup.vehnixauto.server.app.glossary.domain.models

import java.time.LocalDateTime

data class GlossaryEntry(
    val glossaryId: Long? = null,
    val officialName: String,
    val category: String,
    val localNames: List<String> = emptyList(),
    val description: String? = null,
    val wearSigns: String? = null,
    val tips: String? = null,
    val createdBy: Long? = null,
    val status: String = "ACTIVE",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val files: List<GlossaryFile> = emptyList(),
)
