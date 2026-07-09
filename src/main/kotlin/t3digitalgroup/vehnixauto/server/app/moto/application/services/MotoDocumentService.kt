package t3digitalgroup.vehnixauto.server.app.moto.application.services

import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import t3digitalgroup.vehnixauto.server.app.car.application.services.DocumentService
import t3digitalgroup.vehnixauto.server.app.moto.domain.models.MotoDocument
import t3digitalgroup.vehnixauto.server.app.moto.infrastructure.entities.MotoDocumentEntity
import t3digitalgroup.vehnixauto.server.app.moto.infrastructure.mapper.toDomain
import t3digitalgroup.vehnixauto.server.app.moto.infrastructure.repositories.MotoDocumentRepository
import t3digitalgroup.vehnixauto.server.utils.Mode

@Service
@Profile(Mode.DEV)
class MotoDocumentService(
    private val repository: MotoDocumentRepository,
    private val documentService: DocumentService,
) {
    suspend fun linkDocuments(motoId: Long, documentIds: List<Long>) {
        val uniqueIds = documentIds.distinct()
        documentService.validateIds(uniqueIds)
        uniqueIds.forEach { documentId ->
            repository.save(
                MotoDocumentEntity(
                    motoId = motoId,
                    documentId = documentId,
                )
            )
        }
    }

    suspend fun findByMotoIdIn(motoIds: List<Long>): List<MotoDocument> {
        if (motoIds.isEmpty()) return emptyList()
        val links = repository.findByMotoIdIn(motoIds).toList()
        val documentsById = documentService.findByIdIn(links.map { it.documentId })
            .associateBy { it.documentId }
        return links.map { entity ->
            entity.toDomain(documentsById[entity.documentId])
        }
    }
}
