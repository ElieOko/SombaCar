package t3digitalgroup.vehnixauto.server.app.garage.infrastructure.mapper

import t3digitalgroup.vehnixauto.server.app.garage.domain.models.Garage
import t3digitalgroup.vehnixauto.server.app.garage.domain.models.GarageImage
import t3digitalgroup.vehnixauto.server.app.garage.infrastructure.entities.GarageEntity
import t3digitalgroup.vehnixauto.server.app.garage.infrastructure.entities.GarageImageEntity

fun GarageEntity.toDomain(images: List<GarageImage> = emptyList()) = Garage(
    garageId = this.garageId,
    userId = this.userId,
    name = this.name,
    description = this.description,
    address = this.address,
    city = this.city,
    country = this.country,
    phone = this.phone,
    latitude = this.latitude,
    longitude = this.longitude,
    garageType = this.garageType,
    isActive = this.isActive,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    images = images,
)

fun Garage.toEntity() = GarageEntity(
    garageId = this.garageId,
    userId = this.userId,
    name = this.name,
    description = this.description,
    address = this.address,
    city = this.city,
    country = this.country,
    phone = this.phone,
    latitude = this.latitude,
    longitude = this.longitude,
    garageType = this.garageType,
    isActive = this.isActive,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

fun GarageImageEntity.toDomain() = GarageImage(
    garageImageId = this.id,
    garageId = this.garageId,
    name = this.name,
    path = this.path,
)
