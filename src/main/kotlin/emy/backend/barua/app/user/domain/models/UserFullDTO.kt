package emy.backend.barua.app.user.domain.models

import emy.backend.barua.app.user.infrastructure.entities.AccountDTO

data class UserFullDTO(
    val user: UserDto,
    val accounts: List<AccountDTO>,
//    val profile: Person?
)
