package t3digitalgroup.vehnixauto.server.app.garage.infrastructure.mapper

import t3digitalgroup.vehnixauto.server.app.garage.domain.models.Garage
import t3digitalgroup.vehnixauto.server.app.garage.infrastructure.entities.GarageEntity

fun GarageEntity.toDomain() = Garage(
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
