package t3digitalgroup.vehnixauto.server.app.catalog.infrastructure.mapper

import t3digitalgroup.vehnixauto.server.app.catalog.domain.models.TypeCard
import t3digitalgroup.vehnixauto.server.app.catalog.infrastructure.entities.TypeCardEntity

fun TypeCardEntity.toDomain() = TypeCard(typeCardId = this.typeCardId, name = this.name)

fun TypeCard.toEntity() = TypeCardEntity(typeCardId = this.typeCardId, name = this.name)
