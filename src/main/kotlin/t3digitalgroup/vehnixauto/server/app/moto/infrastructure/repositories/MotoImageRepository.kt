package t3digitalgroup.vehnixauto.server.app.moto.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.moto.infrastructure.entities.MotoImageEntity

interface MotoImageRepository : CoroutineCrudRepository<MotoImageEntity, Long> {
    fun findByMotoIdIn(motoIds: List<Long>): Flow<MotoImageEntity>
}
