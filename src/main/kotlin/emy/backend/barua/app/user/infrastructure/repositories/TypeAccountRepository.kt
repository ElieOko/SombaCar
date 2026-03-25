package emy.backend.barua.app.user.infrastructure.repositories

import emy.backend.barua.app.user.infrastructure.entities.TypeAccountEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface TypeAccountRepository : CoroutineCrudRepository<TypeAccountEntity, Long>