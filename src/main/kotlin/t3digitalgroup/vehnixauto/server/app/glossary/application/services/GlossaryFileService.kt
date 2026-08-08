package t3digitalgroup.vehnixauto.server.app.glossary.application.services

import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.adaptater.provider.gcs.GcsService
import t3digitalgroup.vehnixauto.server.app.glossary.infrastructure.entities.GlossaryFileEntity
import t3digitalgroup.vehnixauto.server.app.glossary.infrastructure.mapper.toDomain
import t3digitalgroup.vehnixauto.server.app.glossary.infrastructure.repositories.GlossaryFileRepository
import t3digitalgroup.vehnixauto.server.utils.Mode

@Service
@Profile(Mode.DEV)
class GlossaryFileService(
    private val repository: GlossaryFileRepository,
    private val gcsService: GcsService,
) {
    private val subdirectory = "glossary/"

    suspend fun createFromFile(glossaryId: Long, file: MultipartFile): GlossaryFileEntity {
        val fileUri = gcsService.uploadFile(file, subdirectory)
            ?: throw IllegalStateException("Échec de l'upload du fichier")
        return repository.save(
            GlossaryFileEntity(
                glossaryId = glossaryId,
                name = file.originalFilename ?: file.name,
                path = fileUri,
                mimeType = file.contentType,
            )
        )
    }

    suspend fun createFromFiles(glossaryId: Long, files: List<MultipartFile>): List<GlossaryFileEntity> =
        files.filter { !it.isEmpty }.map { createFromFile(glossaryId, it) }

    suspend fun findByGlossaryIdIn(glossaryIds: List<Long>) =
        repository.findByGlossaryIdIn(glossaryIds).toList().map { it.toDomain() }

    suspend fun deleteById(glossaryId: Long, fileId: Long): Boolean {
        val file = repository.findById(fileId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Fichier introuvable.")
        if (file.glossaryId != glossaryId) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce fichier n'appartient pas à cette entrée du glossaire.")
        }
        repository.deleteById(fileId)
        return true
    }
}
