package server.web.casa.app.user.domain.model

import server.web.casa.app.user.infrastructure.persistence.entity.AccountEntity

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