package initializer.backend.spring.app.user.domain.models

import initializer.backend.spring.app.user.infrastructure.entities.AccountDTO


data class UserFullDTO(
    val user: UserDto,
    val accounts: List<AccountDTO>,
//    val profile: Person?
)
