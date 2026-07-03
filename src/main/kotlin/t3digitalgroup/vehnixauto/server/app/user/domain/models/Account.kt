package t3digitalgroup.vehnixauto.server.app.user.domain.models

import t3digitalgroup.vehnixauto.server.app.user.infrastructure.entities.AccountEntity


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