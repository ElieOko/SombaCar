package initializer.backend.spring.app.user.application.services

import initializer.backend.spring.app.user.domain.models.Account
import initializer.backend.spring.app.user.domain.models.toEntity
import initializer.backend.spring.app.user.infrastructure.entities.AccountDTO
import initializer.backend.spring.app.user.infrastructure.entities.toDomain
import initializer.backend.spring.app.user.infrastructure.repositories.AccountRepository
import initializer.backend.spring.utils.Mode
import kotlinx.coroutines.flow.*
import org.springframework.context.annotation.Profile
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
@Profile(Mode.DEV)
class AccountService(
    private val repository: AccountRepository,
    private val typeAccountService: TypeAccountService
) {
    suspend fun save(data: Account): Account {
        val data = data.toEntity()
        val result = repository.save(data)
        return result.toDomain()
    }
    suspend fun getAll() = repository.findAll().map {
        AccountDTO(
            id = it.id,
            name = it.name,
            typeAccount = typeAccountService.findByIdTypeAccount(it.typeAccountId)
        )
    }

    suspend fun findByIdAccount(id : Long): Account {
      val data = repository.findById(id)?: throw ResponseStatusException(HttpStatusCode.valueOf(404), "ID Is Not Found.")
        return data.toDomain()
    }
    suspend fun findAccountWithType(account : Long, type : Long): Account {
      val data = repository.findAll().filter { it.typeAccountId == account && it.typeAccountId == type }.toList()
      if (data.isEmpty()) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Ce compte et type ne sont pas prise en charge.")
      return data.first().toDomain()
    }
    suspend fun isAllow(accountId : Long):Boolean = if (accountId == 19L || accountId == 18L) true else false

}