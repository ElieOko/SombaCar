package t3digitalgroup.vehnixauto.server.app.glossary.domain.models.request

import jakarta.validation.constraints.NotBlank

data class GlossaryEntryRequest(
    @NotBlank
    val officialName: String,
    @NotBlank
    val category: String,
    val localNames: List<String> = emptyList(),
    val description: String? = null,
    val wearSigns: String? = null,
    val tips: String? = null,
)
