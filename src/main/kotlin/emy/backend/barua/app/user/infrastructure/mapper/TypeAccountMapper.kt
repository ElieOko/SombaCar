package emy.backend.barua.app.user.infrastructure.mapper

import emy.backend.barua.app.user.domain.models.TypeAccount
import emy.backend.barua.app.user.infrastructure.entities.TypeAccountEntity

fun TypeAccountEntity.toDomain(): TypeAccount {
    val e = this
    return TypeAccount(
        typeAccountId = e.id,
        name = e.name,
    )
}

fun TypeAccount.toEntity(): TypeAccountEntity {
    val e = this
    return TypeAccountEntity(
        id = e.typeAccountId,
        name = e.name
    )
}