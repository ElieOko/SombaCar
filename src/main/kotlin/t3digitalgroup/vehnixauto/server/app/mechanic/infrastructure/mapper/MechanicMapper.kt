package t3digitalgroup.vehnixauto.server.app.mechanic.infrastructure.mapper

import t3digitalgroup.vehnixauto.server.app.mechanic.domain.models.Mechanic
import t3digitalgroup.vehnixauto.server.app.mechanic.domain.models.MechanicContactRequest
import t3digitalgroup.vehnixauto.server.app.mechanic.domain.models.MechanicImage
import t3digitalgroup.vehnixauto.server.app.mechanic.infrastructure.entities.MechanicContactEntity
import t3digitalgroup.vehnixauto.server.app.mechanic.infrastructure.entities.MechanicEntity
import t3digitalgroup.vehnixauto.server.app.mechanic.infrastructure.entities.MechanicImageEntity

fun MechanicEntity.toDomain(images: List<MechanicImage> = emptyList()) = Mechanic(
    mechanicId = this.mechanicId,
    garageId = this.garageId,
    fullName = this.fullName,
    phone = this.phone,
    city = this.city,
    latitude = this.latitude,
    longitude = this.longitude,
    isNightAvailable = this.isNightAvailable,
    isActive = this.isActive,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    images = images,
)

fun Mechanic.toEntity() = MechanicEntity(
    mechanicId = this.mechanicId,
    garageId = this.garageId,
    fullName = this.fullName,
    phone = this.phone,
    city = this.city,
    latitude = this.latitude,
    longitude = this.longitude,
    isNightAvailable = this.isNightAvailable,
    isActive = this.isActive,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

fun MechanicContactEntity.toDomain() = MechanicContactRequest(
    contactId = this.contactId,
    userId = this.userId,
    mechanicId = this.mechanicId,
    message = this.message,
    status = this.status,
    createdAt = this.createdAt,
)

fun MechanicContactRequest.toEntity() = MechanicContactEntity(
    contactId = this.contactId,
    userId = this.userId,
    mechanicId = this.mechanicId,
    message = this.message,
    status = this.status,
    createdAt = this.createdAt,
)

fun MechanicImageEntity.toDomain() = MechanicImage(
    mechanicImageId = this.id,
    mechanicId = this.mechanicId,
    name = this.name,
    path = this.path,
)
