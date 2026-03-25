package emy.backend.barua.app.user.domain.models

import emy.backend.barua.app.user.infrastructure.entities.AccountUserEntity


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