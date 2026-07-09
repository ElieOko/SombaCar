package t3digitalgroup.vehnixauto.server.app.moto.infrastructure.mapper

import t3digitalgroup.vehnixauto.server.app.car.domain.models.Document
import t3digitalgroup.vehnixauto.server.app.moto.domain.models.*
import t3digitalgroup.vehnixauto.server.app.moto.infrastructure.entities.*

fun MotoModelEntity.toDomain() = MotoModel(
    motoModelId = this.motoModelId,
    brand = this.brand,
    model = this.model,
    generation = this.generation,
    bodyType = this.bodyType,
    createdAt = this.createdAt,
)

fun MotoModel.toEntity() = MotoModelEntity(
    motoModelId = this.motoModelId,
    brand = this.brand,
    model = this.model,
    generation = this.generation,
    bodyType = this.bodyType,
    createdAt = this.createdAt,
)

fun MotoListingEntity.toDomain(
    motoModel: MotoModel? = null,
    images: List<MotoImage> = emptyList(),
    documents: List<MotoDocument>? = null,
) = MotoListing(
    listingId = this.listingId,
    userId = this.userId,
    motoModelId = this.motoModelId,
    listingType = this.listingType,
    year = this.year,
    isElectric = this.isElectric,
    mileageKm = this.mileageKm,
    condition = this.condition,
    plateNumber = this.plateNumber,
    numberVin = this.numberVin,
    color = this.color,
    engineCc = this.engineCc,
    price = this.price,
    devise = this.deviseId,
    rentPricePerDay = this.rentPricePerDay,
    exchangeDescription = this.exchangeDescription,
    description = this.description,
    city = this.city,
    country = this.country,
    status = this.status,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    motoModel = motoModel,
    images = images,
    documents = documents,
)

fun MotoListing.toEntity() = MotoListingEntity(
    listingId = this.listingId,
    userId = this.userId,
    motoModelId = this.motoModelId,
    listingType = this.listingType,
    year = this.year,
    isElectric = this.isElectric,
    mileageKm = this.mileageKm,
    condition = this.condition,
    plateNumber = this.plateNumber,
    numberVin = this.numberVin,
    color = this.color,
    engineCc = this.engineCc,
    price = this.price,
    deviseId = this.devise,
    rentPricePerDay = this.rentPricePerDay,
    exchangeDescription = this.exchangeDescription,
    description = this.description,
    city = this.city,
    country = this.country,
    status = this.status,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

fun MotoImageEntity.toDomain() = MotoImage(
    motoImageId = this.id,
    motoId = this.motoId,
    name = this.name,
    path = this.path,
)

fun MotoDocumentEntity.toDomain(document: Document? = null) = MotoDocument(
    motoDocumentId = this.id,
    motoId = this.motoId,
    documentId = this.documentId,
    document = document,
)
