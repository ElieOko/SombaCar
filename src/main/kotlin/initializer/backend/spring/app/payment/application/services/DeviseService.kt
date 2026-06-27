package initializer.backend.spring.app.payment.application.services

import initializer.backend.spring.app.payment.domain.models.Devise
import initializer.backend.spring.app.payment.infrastructure.mapper.toDomain
import initializer.backend.spring.app.payment.infrastructure.mapper.toEntity
import initializer.backend.spring.app.payment.infrastructure.repositories.DeviseRepository
import initializer.backend.spring.utils.Mode
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
@Profile(Mode.DEV)
class DeviseService(
    private val repository: DeviseRepository
) {
    suspend fun create(data: Devise) = repository.save(data.toEntity()).toDomain()

    suspend fun getAllData(): List<Devise> {
        return repository.findAll().map { it.toDomain() }.toList()
    }

    suspend fun getById(id: Long?): Devise? {
        if (id != null) {
            val data = repository.findById(id) ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette devise n'existe pas")
            return data.toDomain()
        }
        return null
    }
}
