package initializer.backend.spring.app.user.application.services

import initializer.backend.spring.app.user.domain.models.TypeAccount
import initializer.backend.spring.app.user.infrastructure.entities.TypeAccountEntity
import initializer.backend.spring.app.user.infrastructure.mapper.toDomain
import initializer.backend.spring.app.user.infrastructure.mapper.toEntity
import initializer.backend.spring.app.user.infrastructure.repositories.TypeAccountRepository
import initializer.backend.spring.utils.Mode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

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