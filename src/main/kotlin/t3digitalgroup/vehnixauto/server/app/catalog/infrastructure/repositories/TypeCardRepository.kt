package t3digitalgroup.vehnixauto.server.app.catalog.infrastructure.repositories

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.catalog.infrastructure.entities.TypeCardEntity

interface TypeCardRepository : CoroutineCrudRepository<TypeCardEntity, Long>
