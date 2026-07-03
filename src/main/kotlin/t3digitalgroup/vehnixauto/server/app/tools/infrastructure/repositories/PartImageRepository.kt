package t3digitalgroup.vehnixauto.server.app.tools.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.tools.infrastructure.entities.PartImageEntity

interface PartImageRepository : CoroutineCrudRepository<PartImageEntity, Long> {
    fun findByPartListingIdIn(partListingIds: List<Long>): Flow<PartImageEntity>
}
