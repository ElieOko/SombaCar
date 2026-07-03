package t3digitalgroup.vehnixauto.server.app.user.infrastructure.mapper

import t3digitalgroup.vehnixauto.server.app.user.domain.models.User
import t3digitalgroup.vehnixauto.server.app.user.domain.models.UserDto
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.entities.UserEntity
import kotlin.time.ExperimentalTime

fun UserEntity.toDomain(): UserDto {
    val entity = this
    return UserDto(
        userId = entity.userId,
        email = entity.email,
        phone = entity.phone.toString(),
        username = entity.username.toString(),
        city = entity.city,
        isPremium = entity.isPremium,
        isCertified = entity.isCertified,
        firstName = entity.firstName,
        lastName = entity.lastName,
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
//        country = user.country,
        isPremium = user.isPremium,
        firstName = user.firstName,
        lastName = user.lastName,
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
        firstName = user.firstName,
        lastName = user.lastName,
    )
}

