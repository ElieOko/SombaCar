package t3digitalgroup.vehnixauto.server.app.glossary.application.services

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.glossary.domain.models.GlossaryEntry
import t3digitalgroup.vehnixauto.server.app.glossary.domain.models.request.GlossaryEntryRequest
import t3digitalgroup.vehnixauto.server.app.glossary.domain.models.request.GlossaryEntryUpdateRequest
import t3digitalgroup.vehnixauto.server.app.glossary.infrastructure.mapper.encodeLocalNames
import t3digitalgroup.vehnixauto.server.app.glossary.infrastructure.mapper.toDomain
import t3digitalgroup.vehnixauto.server.app.glossary.infrastructure.mapper.toEntity
import t3digitalgroup.vehnixauto.server.app.glossary.infrastructure.repositories.GlossaryEntryRepository
import t3digitalgroup.vehnixauto.server.utils.Mode
import java.time.LocalDateTime

@Service
@Profile(Mode.DEV)
class GlossaryService(
    private val repository: GlossaryEntryRepository,
    private val glossaryFileService: GlossaryFileService,
) {
    suspend fun create(request: GlossaryEntryRequest, createdBy: Long): GlossaryEntry {
        val entity = GlossaryEntry(
            officialName = request.officialName.trim(),
            category = request.category.trim(),
            localNames = request.localNames,
            description = request.description?.trim(),
            wearSigns = request.wearSigns?.trim(),
            tips = request.tips?.trim(),
            createdBy = createdBy,
        ).toEntity()
        return repository.save(entity).toDomain()
    }

    suspend fun findById(id: Long): GlossaryEntry {
        val entry = repository.findById(id)?.toDomain()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Entrée du glossaire introuvable.")
        return enrichEntries(listOf(entry)).first()
    }

    suspend fun findAllActive(): List<GlossaryEntry> {
        val entries = repository.findAllActive().map { it.toDomain() }.toList()
        return enrichEntries(entries)
    }

    suspend fun findAll(): List<GlossaryEntry> {
        val entries = repository.findAllOrdered().map { it.toDomain() }.toList()
        return enrichEntries(entries)
    }

    suspend fun findByCategory(category: String): List<GlossaryEntry> {
        val entries = repository.findActiveByCategory(category.trim()).map { it.toDomain() }.toList()
        return enrichEntries(entries)
    }

    suspend fun search(query: String): List<GlossaryEntry> {
        val entries = repository.searchActive(query.trim()).map { it.toDomain() }.toList()
        return enrichEntries(entries)
    }

    suspend fun update(id: Long, request: GlossaryEntryUpdateRequest): GlossaryEntry {
        val entity = repository.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Entrée du glossaire introuvable.")
        entity.officialName = request.officialName.trim()
        entity.category = request.category.trim()
        entity.localNames = encodeLocalNames(request.localNames)
        entity.description = request.description?.trim()
        entity.wearSigns = request.wearSigns?.trim()
        entity.tips = request.tips?.trim()
        entity.status = request.status
        entity.updatedAt = LocalDateTime.now()
        val saved = repository.save(entity).toDomain()
        return enrichEntries(listOf(saved)).first()
    }

    suspend fun delete(id: Long): Boolean {
        if (!repository.existsById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Entrée du glossaire introuvable.")
        }
        repository.deleteById(id)
        return true
    }

    private suspend fun enrichEntries(entries: List<GlossaryEntry>): List<GlossaryEntry> {
        if (entries.isEmpty()) return entries
        val ids = entries.mapNotNull { it.glossaryId }
        if (ids.isEmpty()) return entries

        val filesByGlossaryId = glossaryFileService.findByGlossaryIdIn(ids)
            .groupBy { it.glossaryId }

        return entries.map { entry ->
            entry.copy(files = filesByGlossaryId[entry.glossaryId].orEmpty())
        }
    }
}
