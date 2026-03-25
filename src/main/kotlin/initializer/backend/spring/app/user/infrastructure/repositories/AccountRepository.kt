package initializer.backend.spring.app.user.infrastructure.repositories

import initializer.backend.spring.app.user.infrastructure.entities.AccountEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface AccountRepository : CoroutineCrudRepository<AccountEntity, Long>