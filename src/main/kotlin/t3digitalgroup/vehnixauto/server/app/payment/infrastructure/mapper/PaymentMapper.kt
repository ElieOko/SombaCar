package t3digitalgroup.vehnixauto.server.app.payment.infrastructure.mapper

import t3digitalgroup.vehnixauto.server.app.payment.domain.models.Devise
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.Paiement
import t3digitalgroup.vehnixauto.server.app.payment.infrastructure.entities.DeviseEntity
import t3digitalgroup.vehnixauto.server.app.payment.infrastructure.entities.PaiementEntity


fun PaiementEntity.toDomain() = Paiement(
    id = this.id,
    userId = this.userId,
    reference = this.reference,
    amount = this.amount,
    devise = this.devise,
    description = this.description,
    typePayment = this.typePayment,
    status = this.status,
    dateCreated = this.dateCreated,
    dateUpdated = this.dateUpdated
)

fun Paiement.toEntity() = PaiementEntity(
    id = this.id,
    userId = this.userId,
    reference = this.reference,
    amount = this.amount,
    devise = this.devise,
    description = this.description,
    typePayment = this.typePayment,
    status = this.status,
    dateCreated = this.dateCreated,
    dateUpdated = this.dateUpdated
)

fun DeviseEntity.toDomain() = Devise(
    id = this.id,
    name = this.name,
    code = this.code,
    tauxLocal = this.tauxLocal
)

fun Devise.toEntity() = DeviseEntity(
    id = this.id,
    name = this.name,
    code = this.code,
    tauxLocal = this.tauxLocal
)
