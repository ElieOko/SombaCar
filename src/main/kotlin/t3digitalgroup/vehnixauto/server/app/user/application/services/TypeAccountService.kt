package t3digitalgroup.vehnixauto.server.app.user.application.services

import kotlinx.coroutines.flow.*
import org.springframework.context.annotation.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.server.*
import t3digitalgroup.vehnixauto.server.app.user.domain.models.TypeAccount
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.entities.TypeAccountEntity
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.mapper.toDomain
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.mapper.toEntity
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.repositories.TypeAccountRepository
import t3digitalgroup.vehnixauto.server.utils.Mode


@Service
@Profile(Mode.DEV)
class TypeAccountService(
    private val repository: TypeAccountRepository,
) {
    suspend fun saveAccount(data: TypeAccount): TypeAccount {
        val data = data.toEntity()
        val result = repository.save(data)
        return result.toDomain()
    }
    suspend fun getAll(): Flow<TypeAccount> {
        val data= repository.findAll()
        return data.map {
            TypeAccountEntity(it.id, it.name).toDomain()
        }
    }
    suspend fun findByIdTypeAccount(id : Long) : TypeAccount {
      val data = repository.findById(id) ?: throw ResponseStatusException(
          HttpStatusCode.valueOf(404),
          "ID Is Not Found."
      )
        return data.toDomain()
    }
}