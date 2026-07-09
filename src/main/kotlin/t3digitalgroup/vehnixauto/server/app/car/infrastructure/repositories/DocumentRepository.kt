package t3digitalgroup.vehnixauto.server.app.car.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.car.infrastructure.entities.DocumentEntity

interface DocumentRepository : CoroutineCrudRepository<DocumentEntity, Long> {
    fun findByIdIn(ids: List<Long>): Flow<DocumentEntity>
}
