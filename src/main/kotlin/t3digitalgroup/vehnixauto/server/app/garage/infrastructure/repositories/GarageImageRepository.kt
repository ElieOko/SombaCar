package t3digitalgroup.vehnixauto.server.app.garage.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.garage.infrastructure.entities.GarageImageEntity

interface GarageImageRepository : CoroutineCrudRepository<GarageImageEntity, Long> {
    fun findByGarageIdIn(garageIds: List<Long>): Flow<GarageImageEntity>
}
