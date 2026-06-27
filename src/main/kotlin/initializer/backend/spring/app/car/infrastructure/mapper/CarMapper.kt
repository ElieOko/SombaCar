package initializer.backend.spring.app.car.infrastructure.mapper

import initializer.backend.spring.app.car.domain.models.CarListing
import initializer.backend.spring.app.car.domain.models.CarModel
import initializer.backend.spring.app.car.infrastructure.entities.CarListingEntity
import initializer.backend.spring.app.car.infrastructure.entities.CarModelEntity

fun CarModelEntity.toDomain() = CarModel(
    carModelId = this.carModelId,
    brand = this.brand,
    model = this.model,
    generation = this.generation,
    bodyType = this.bodyType,
    createdAt = this.createdAt
)

fun CarModel.toEntity() = CarModelEntity(
    carModelId = this.carModelId,
    brand = this.brand,
    model = this.model,
    generation = this.generation,
    bodyType = this.bodyType,
    createdAt = this.createdAt
)

fun CarListingEntity.toDomain(carModel: CarModel? = null) = CarListing(
    listingId = this.listingId,
    userId = this.userId,
    carModelId = this.carModelId,
    listingType = this.listingType,
    year = this.year,
    isElectric = this.isElectric,
    mileageKm = this.mileageKm,
    condition = this.condition,
    plateNumber = this.plateNumber,
    color = this.color,
    seats = this.seats,
    price = this.price,
    rentPricePerDay = this.rentPricePerDay,
    exchangeDescription = this.exchangeDescription,
    description = this.description,
    city = this.city,
    country = this.country,
    status = this.status,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    carModel = carModel
)

fun CarListing.toEntity() = CarListingEntity(
    listingId = this.listingId,
    userId = this.userId,
    carModelId = this.carModelId,
    listingType = this.listingType,
    year = this.year,
    isElectric = this.isElectric,
    mileageKm = this.mileageKm,
    condition = this.condition,
    plateNumber = this.plateNumber,
    color = this.color,
    seats = this.seats,
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
