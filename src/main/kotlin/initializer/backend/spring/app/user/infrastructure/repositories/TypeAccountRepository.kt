package initializer.backend.spring.app.user.infrastructure.repositories

import initializer.backend.spring.app.user.infrastructure.entities.TypeAccountEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface TypeAccountRepository : CoroutineCrudRepository<TypeAccountEntity, Long>