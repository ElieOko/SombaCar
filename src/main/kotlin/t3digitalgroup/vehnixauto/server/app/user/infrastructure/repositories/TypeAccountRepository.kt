package t3digitalgroup.vehnixauto.server.app.user.infrastructure.repositories

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.entities.TypeAccountEntity

interface TypeAccountRepository : CoroutineCrudRepository<TypeAccountEntity, Long>