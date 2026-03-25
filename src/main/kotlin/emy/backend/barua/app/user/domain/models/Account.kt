package emy.backend.barua.app.user.domain.models

import emy.backend.barua.app.user.infrastructure.entities.AccountEntity

data class Account(
    val id : Long? = null,
    val name: String,
    val typeAccountId : Long
)

fun Account.toEntity() = AccountEntity(
    id = this.id,
    name = this.name,
    typeAccountId = this.typeAccountId,
)