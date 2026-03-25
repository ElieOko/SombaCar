package server.web.casa.app.user.domain.model

import server.web.casa.app.actor.domain.model.Person
import server.web.casa.app.user.infrastructure.persistence.entity.AccountDTO

data class UserFullDTO(
    val user: UserDto,
    val accounts: List<AccountDTO>,
    val profile: Person?
)
