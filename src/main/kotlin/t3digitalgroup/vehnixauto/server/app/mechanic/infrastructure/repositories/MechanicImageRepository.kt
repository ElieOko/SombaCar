package t3digitalgroup.vehnixauto.server.app.mechanic.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.mechanic.infrastructure.entities.MechanicImageEntity

interface MechanicImageRepository : CoroutineCrudRepository<MechanicImageEntity, Long> {
    fun findByMechanicIdIn(mechanicIds: List<Long>): Flow<MechanicImageEntity>
}
