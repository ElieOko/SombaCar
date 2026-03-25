package server.web.casa.app.user.domain.model

import server.web.casa.app.user.infrastructure.persistence.entity.AccountUserEntity

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