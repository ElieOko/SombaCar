package t3digitalgroup.vehnixauto.server.app.glossary.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.glossary.infrastructure.entities.GlossaryFileEntity

interface GlossaryFileRepository : CoroutineCrudRepository<GlossaryFileEntity, Long> {
    fun findByGlossaryIdIn(glossaryIds: List<Long>): Flow<GlossaryFileEntity>
}
