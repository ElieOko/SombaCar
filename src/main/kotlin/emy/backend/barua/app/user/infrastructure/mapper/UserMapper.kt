package emy.backend.barua.app.user.infrastructure.mapper

import emy.backend.barua.app.user.domain.models.User
import emy.backend.barua.app.user.domain.models.UserDto
import emy.backend.barua.app.user.infrastructure.entities.UserEntity
import kotlin.time.ExperimentalTime

fun UserEntity.toDomain(): UserDto {
    val entity = this
    return UserDto(
        userId = entity.userId,
        email = entity.email,
        phone = entity.phone.toString(),
        username = entity.username.toString(),
        city = entity.city.toString(),
        country = entity.country,
        isPremium = entity.isPremium,
        isCertified = entity.isCertified,
    )
}

@OptIn(ExperimentalTime::class)
fun UserDto.toEntityToDto(password: String): UserEntity {
    val user = this
    return UserEntity(
        userId = user.userId,
        username = user.username,
        email = user.email,
        phone = user.phone,
        city = user.city,
        password = password,
        country = user.country,
        isPremium = user.isPremium,
    )
}

@OptIn(ExperimentalTime::class)
fun User.toEntity(): UserEntity {
    val user = this
    return UserEntity(
        userId = user.userId,
        username = user.username,
        email = user.email,
        phone = user.phone,
        city = user.city,
        country = user.country,
    )
}

