package initializer.backend.spring.app.user.infrastructure.repositories

import initializer.backend.spring.app.user.infrastructure.entities.AccountUserEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface AccountUserRepository : CoroutineCrudRepository<AccountUserEntity, Long> {
    @Query("SELECT * FROM account_users WHERE user_id = :userId AND account_id = :accountId")
    suspend fun findByUserAndAccount(userId: Long, accountId : Long) : AccountUserEntity?
    fun findByUserIdIn(userIds: List<Long>): Flow<AccountUserEntity>
    @Query("SELECT * FROM account_users WHERE user_id = :userId")
    fun findAllAccountByUserId(userId: Long): Flow<AccountUserEntity>
}