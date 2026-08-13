package t3digitalgroup.vehnixauto.server.app.mechanic.infrastructure.mapper

import t3digitalgroup.vehnixauto.server.app.mechanic.domain.models.Mechanic
import t3digitalgroup.vehnixauto.server.app.mechanic.domain.models.MechanicContactRequest
import t3digitalgroup.vehnixauto.server.app.mechanic.infrastructure.entities.MechanicContactEntity
import t3digitalgroup.vehnixauto.server.app.mechanic.infrastructure.entities.MechanicEntity

fun MechanicEntity.toDomain() = Mechanic(
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
