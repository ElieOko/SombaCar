package t3digitalgroup.vehnixauto.server.app.user.infrastructure.repositories

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.entities.AccountEntity

interface AccountRepository : CoroutineCrudRepository<AccountEntity, Long>