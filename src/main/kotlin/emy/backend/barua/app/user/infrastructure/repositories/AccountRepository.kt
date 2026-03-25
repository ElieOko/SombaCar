package emy.backend.barua.app.user.infrastructure.repositories

import emy.backend.barua.app.user.infrastructure.entities.AccountEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository


interface AccountRepository : CoroutineCrudRepository<AccountEntity, Long>