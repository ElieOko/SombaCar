package t3digitalgroup.vehnixauto.server.app.user.application.services

import kotlinx.coroutines.flow.*
import org.springframework.context.annotation.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.server.*
import t3digitalgroup.vehnixauto.server.app.user.domain.models.Account
import t3digitalgroup.vehnixauto.server.app.user.domain.models.toEntity
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.entities.AccountDTO
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.entities.toDomain
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.repositories.AccountRepository
import t3digitalgroup.vehnixauto.server.utils.Mode

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
    suspend fun isAllow(accountId : Long):Boolean = if (accountId == 2L || accountId == 3L) true else false

}