package t3digitalgroup.vehnixauto.server.app.moto.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.moto.infrastructure.entities.MotoDocumentEntity

interface MotoDocumentRepository : CoroutineCrudRepository<MotoDocumentEntity, Long> {
    fun findByMotoIdIn(motoIds: List<Long>): Flow<MotoDocumentEntity>
}
