package t3digitalgroup.vehnixauto.server.app.car.application.services

import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.car.domain.models.Document
import t3digitalgroup.vehnixauto.server.app.car.infrastructure.mapper.toDomain
import t3digitalgroup.vehnixauto.server.app.car.infrastructure.repositories.DocumentRepository
import t3digitalgroup.vehnixauto.server.utils.Mode

@Service
@Profile(Mode.DEV)
class DocumentService(
    private val repository: DocumentRepository,
) {
    suspend fun findAll(): List<Document> = repository.findAll().toList().map { it.toDomain() }

    suspend fun findById(id: Long): Document =
        repository.findById(id)?.toDomain()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document introuvable.")

    suspend fun findByIdIn(ids: List<Long>): List<Document> {
        if (ids.isEmpty()) return emptyList()
        return repository.findByIdIn(ids).toList().map { it.toDomain() }
    }

    suspend fun validateIds(documentIds: List<Long>) {
        if (documentIds.isEmpty()) return
        val found = findByIdIn(documentIds.distinct())
        if (found.size != documentIds.distinct().size) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Un ou plusieurs documents sont introuvables.")
        }
    }
}
