package t3digitalgroup.vehnixauto.server.app.user.application.services

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import org.springframework.context.annotation.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.server.*
import t3digitalgroup.vehnixauto.server.app.user.domain.models.AccountUser
import t3digitalgroup.vehnixauto.server.app.user.domain.models.toEntity
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.entities.toDomain
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.repositories.AccountUserRepository
import t3digitalgroup.vehnixauto.server.utils.Mode


@Service
@Profile(Mode.DEV)
class AccountUserService(
    private val repository: AccountUserRepository,
) {
    suspend fun save(data: AccountUser): AccountUser {
        val data = data.toEntity()
        val result = repository.save(data)
        return result.toDomain()
    }
    suspend fun getAll() = repository.findAll().map { it.toDomain() }
    suspend fun findByIdAccount(id : Long): AccountUser {
      val data = repository.findById(id)?: throw ResponseStatusException(HttpStatusCode.valueOf(404), "ID Is Not Found.")
        return data.toDomain()
    }
    suspend fun findMultipleAccountUser(userId : Long) = coroutineScope{
        repository.findAllAccountByUserId(userId).toList()
    }
//    suspend fun findAccountWithType(account : Long, type : Long): TypeAccountUser {
//      val data = repository.findAll().filter { it.typeAccountId == account && it.typeAccountId == type }.toList()
//      if (data.isEmpty()) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Ce compte et type ne sont pas prise en charge.")
//      return data.first().toDomain()
//    }
}