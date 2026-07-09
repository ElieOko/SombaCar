package t3digitalgroup.vehnixauto.server.app.car.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.car.infrastructure.entities.CarDocumentEntity

interface CarDocumentRepository : CoroutineCrudRepository<CarDocumentEntity, Long> {
    fun findByCarIdIn(carIds: List<Long>): Flow<CarDocumentEntity>
}
