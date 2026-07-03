package t3digitalgroup.vehnixauto.server.app.user.infrastructure.mapper

import t3digitalgroup.vehnixauto.server.app.user.domain.models.TypeAccount
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.entities.TypeAccountEntity

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