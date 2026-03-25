package emy.backend.barua.app.user.application.services

import emy.backend.barua.app.user.domain.models.TypeAccount
import emy.backend.barua.app.user.infrastructure.entities.TypeAccountEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import emy.backend.barua.app.user.infrastructure.repositories.TypeAccountRepository
import emy.backend.barua.utils.Mode
import emy.backend.barua.app.user.infrastructure.mapper.toDomain
import emy.backend.barua.app.user.infrastructure.mapper.toEntity

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