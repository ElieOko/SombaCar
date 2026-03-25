package initializer.backend.spring.app.user.domain.models

import initializer.backend.spring.app.user.infrastructure.entities.AccountUserEntity


data class AccountUser(
    val id: Long? = null,
    val userId: Long,
    val accountId:Long
)
fun AccountUser.toEntity() = AccountUserEntity(
    id = this.id,
    accountId = this.accountId,
    userId = this.userId,
)