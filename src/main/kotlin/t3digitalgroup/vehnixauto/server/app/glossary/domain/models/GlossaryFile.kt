package t3digitalgroup.vehnixauto.server.app.glossary.domain.models

data class GlossaryFile(
    val glossaryFileId: Long? = null,
    val glossaryId: Long? = null,
    val name: String,
    val path: String,
    val mimeType: String? = null,
)
