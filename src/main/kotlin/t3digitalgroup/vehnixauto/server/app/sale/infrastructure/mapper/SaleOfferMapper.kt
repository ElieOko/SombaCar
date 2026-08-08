package t3digitalgroup.vehnixauto.server.app.sale.infrastructure.mapper

import t3digitalgroup.vehnixauto.server.app.sale.domain.models.SaleOffer
import t3digitalgroup.vehnixauto.server.app.sale.infrastructure.entities.SaleOfferEntity

fun SaleOfferEntity.toDomain() = SaleOffer(
    saleOfferId = this.saleOfferId,
    offerType = this.offerType,
    title = this.title,
    description = this.description,
    price = this.price,
    devise = this.devise,
    linkedListingId = this.linkedListingId,
    status = this.status,
    createdBy = this.createdBy,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

fun SaleOffer.toEntity() = SaleOfferEntity(
    saleOfferId = this.saleOfferId,
    offerType = this.offerType,
    title = this.title,
    description = this.description,
    price = this.price,
    devise = this.devise,
    linkedListingId = this.linkedListingId,
    status = this.status,
    createdBy = this.createdBy,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)
