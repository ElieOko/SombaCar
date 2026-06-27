package initializer.backend.spring.app.tools.infrastructure.mapper

import initializer.backend.spring.app.tools.domain.models.PartListing
import initializer.backend.spring.app.tools.infrastructure.entities.PartListingEntity

fun PartListingEntity.toDomain() = PartListing(
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
