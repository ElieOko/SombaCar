package t3digitalgroup.vehnixauto.server.app.user.application.services

import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.user.domain.models.TypeAccount
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
        val normalizedName = data.name.trim()
        if (normalizedName.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nom du type de compte est obligatoire.")
        }
        if (repository.existsByNameIgnoreCase(normalizedName)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Ce type de compte existe déjà.")
        }
        return try {
            repository.save(
                TypeAccount(
                    typeAccountId = null,
                    name = normalizedName,
                ).toEntity(),
            ).toDomain()
        } catch (_: DuplicateKeyException) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Ce type de compte existe déjà.")
        }
    }

    suspend fun getAll(): List<TypeAccount> =
        repository.findAllOrdered().toList().map { it.toDomain() }

    suspend fun findByIdTypeAccount(id: Long): TypeAccount {
        val data = repository.findById(id) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Type de compte introuvable.",
        )
        return data.toDomain()
    }
}
