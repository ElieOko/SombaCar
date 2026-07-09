package t3digitalgroup.vehnixauto.server.app.tools.infrastructure.mapper

import t3digitalgroup.vehnixauto.server.app.tools.domain.models.PartImage
import t3digitalgroup.vehnixauto.server.app.tools.domain.models.PartListing
import t3digitalgroup.vehnixauto.server.app.tools.infrastructure.entities.PartImageEntity
import t3digitalgroup.vehnixauto.server.app.tools.infrastructure.entities.PartListingEntity


fun PartListingEntity.toDomain(images: List<PartImage> = emptyList()) = PartListing(
    partListingId = this.partListingId,
    userId = this.userId,
    name = this.name,
    partReference = this.partReference,
    brand = this.brand,
    compatibleBrand = this.compatibleBrand,
    compatibleModel = this.compatibleModel,
    listingType = this.listingType,
    condition = this.condition,
    price = this.price,
    rentPricePerDay = this.rentPricePerDay,
    exchangeDescription = this.exchangeDescription,
    description = this.description,
    city = this.city,
    country = this.country,
    status = this.status,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    images = images,
)

fun PartListing.toEntity() = PartListingEntity(
    partListingId = this.partListingId,
    userId = this.userId,
    name = this.name,
    partReference = this.partReference,
    brand = this.brand,
    compatibleBrand = this.compatibleBrand,
    compatibleModel = this.compatibleModel,
    listingType = this.listingType,
    condition = this.condition,
    price = this.price,
    rentPricePerDay = this.rentPricePerDay,
    exchangeDescription = this.exchangeDescription,
    description = this.description,
    city = this.city,
    country = this.country,
    status = this.status,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

fun PartImageEntity.toDomain() = PartImage(
    partImageId = this.id,
    partListingId = this.partListingId,
    name = this.name,
    path = this.path,
)
