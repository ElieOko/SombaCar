package t3digitalgroup.vehnixauto.server.app.car.application.services

import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import t3digitalgroup.vehnixauto.server.app.car.domain.models.CarDocument
import t3digitalgroup.vehnixauto.server.app.car.infrastructure.entities.CarDocumentEntity
import t3digitalgroup.vehnixauto.server.app.car.infrastructure.mapper.toDomain
import t3digitalgroup.vehnixauto.server.app.car.infrastructure.repositories.CarDocumentRepository
import t3digitalgroup.vehnixauto.server.utils.Mode

@Service
@Profile(Mode.DEV)
class CarDocumentService(
    private val repository: CarDocumentRepository,
    private val documentService: DocumentService,
) {
    suspend fun linkDocuments(carId: Long, documentIds: List<Long>) {
        val uniqueIds = documentIds.distinct()
        documentService.validateIds(uniqueIds)
        uniqueIds.forEach { documentId ->
            repository.save(
                CarDocumentEntity(
                    carId = carId,
                    documentId = documentId,
                )
            )
        }
    }

    suspend fun findByCarIdIn(carIds: List<Long>): List<CarDocument> {
        if (carIds.isEmpty()) return emptyList()
        val links = repository.findByCarIdIn(carIds).toList()
        val documentsById = documentService.findByIdIn(links.map { it.documentId })
            .associateBy { it.documentId }
        return links.map { entity ->
            entity.toDomain(documentsById[entity.documentId])
        }
    }
}
