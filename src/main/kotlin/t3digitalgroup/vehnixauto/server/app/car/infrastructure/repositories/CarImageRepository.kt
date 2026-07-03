package t3digitalgroup.vehnixauto.server.app.car.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.car.infrastructure.entities.CarImageEntity

interface CarImageRepository : CoroutineCrudRepository<CarImageEntity, Long> {
    fun findByCarIdIn(carIds: List<Long>): Flow<CarImageEntity>
}
