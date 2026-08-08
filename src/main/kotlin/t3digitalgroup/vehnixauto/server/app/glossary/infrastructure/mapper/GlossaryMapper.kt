package t3digitalgroup.vehnixauto.server.app.glossary.infrastructure.mapper

import t3digitalgroup.vehnixauto.server.app.glossary.domain.models.GlossaryEntry
import t3digitalgroup.vehnixauto.server.app.glossary.domain.models.GlossaryFile
import t3digitalgroup.vehnixauto.server.app.glossary.infrastructure.entities.GlossaryEntryEntity
import t3digitalgroup.vehnixauto.server.app.glossary.infrastructure.entities.GlossaryFileEntity
import tools.jackson.module.kotlin.jacksonObjectMapper
import tools.jackson.module.kotlin.readValue

private val jsonMapper = jacksonObjectMapper()

fun encodeLocalNames(localNames: List<String>): String =
    jsonMapper.writeValueAsString(localNames.distinct().filter { it.isNotBlank() })

fun decodeLocalNames(raw: String?): List<String> {
    if (raw.isNullOrBlank()) return emptyList()
    return try {
        jsonMapper.readValue<List<String>>(raw)
    } catch (_: Exception) {
        emptyList()
    }
}

fun GlossaryEntryEntity.toDomain(files: List<GlossaryFile> = emptyList()) = GlossaryEntry(
    glossaryId = this.glossaryId,
    officialName = this.officialName,
    category = this.category,
    localNames = decodeLocalNames(this.localNames),
    description = this.description,
    wearSigns = this.wearSigns,
    tips = this.tips,
    createdBy = this.createdBy,
    status = this.status,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    files = files,
)

fun GlossaryEntry.toEntity() = GlossaryEntryEntity(
    glossaryId = this.glossaryId,
    officialName = this.officialName,
    category = this.category,
    localNames = encodeLocalNames(this.localNames),
    description = this.description,
    wearSigns = this.wearSigns,
    tips = this.tips,
    createdBy = this.createdBy,
    status = this.status,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

fun GlossaryFileEntity.toDomain() = GlossaryFile(
    glossaryFileId = this.glossaryFileId,
    glossaryId = this.glossaryId,
    name = this.name,
    path = this.path,
    mimeType = this.mimeType,
)
