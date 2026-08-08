package t3digitalgroup.vehnixauto.server.app.catalog.application.services

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.catalog.domain.models.TypeCard
import t3digitalgroup.vehnixauto.server.app.catalog.infrastructure.mapper.toDomain
import t3digitalgroup.vehnixauto.server.app.catalog.infrastructure.mapper.toEntity
import t3digitalgroup.vehnixauto.server.app.catalog.infrastructure.repositories.TypeCardRepository
import t3digitalgroup.vehnixauto.server.utils.Mode

@Service
@Profile(Mode.DEV)
class TypeCardService(
    private val repository: TypeCardRepository,
) {
    suspend fun create(typeCard: TypeCard): TypeCard =
        repository.save(typeCard.toEntity()).toDomain()

    suspend fun findAll(): List<TypeCard> =
        repository.findAll().map { it.toDomain() }.toList()

    suspend fun findById(id: Long): TypeCard =
        repository.findById(id)?.toDomain()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Type de carte introuvable.")

    suspend fun update(id: Long, typeCard: TypeCard): TypeCard {
        val entity = repository.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Type de carte introuvable.")
        entity.name = typeCard.name.trim()
        return repository.save(entity).toDomain()
    }

    suspend fun delete(id: Long): Boolean {
        if (!repository.existsById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Type de carte introuvable.")
        }
        repository.deleteById(id)
        return true
    }
}
