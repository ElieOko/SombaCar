package t3digitalgroup.vehnixauto.server.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import t3digitalgroup.vehnixauto.server.app.user.application.services.AccountService
import t3digitalgroup.vehnixauto.server.app.user.application.services.AccountUserService
import t3digitalgroup.vehnixauto.server.app.user.domain.models.UserDto
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.mapper.toDomain
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.repositories.UserRepository

import java.security.Principal

@Service
class Auth(
    private val repository : UserRepository,
    private val account : AccountService,
    private val mutlipleAccount : AccountUserService
) {
    suspend fun userId(): Long? =
        SecurityContextHolder.getContext().authentication?.name?.let(JwtUserIdSupport::parseSubject)

    suspend fun user(): Pair<UserDto?, MutableList<Boolean>>? {
        val allowList = mutableListOf<Boolean>()
        val userId = userId() ?: return null
        val data = repository.findById(userId)
        mutlipleAccount.findMultipleAccountUser(userId).forEach { c ->
            allowList.add(account.isAllow(c.accountId))
        }
        return Pair(data?.toDomain(), allowList)
    }

    suspend fun userStom(principal: Principal): UserDto? {
        val data = repository.findById(JwtUserIdSupport.parseSubject(principal.name))
        return data?.toDomain()
    }
}