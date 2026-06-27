package initializer.backend.spring.app.car.application.services

import initializer.backend.spring.app.car.domain.models.CarModel
import initializer.backend.spring.app.car.domain.models.request.CarModelRequest
import initializer.backend.spring.app.car.infrastructure.mapper.toDomain
import initializer.backend.spring.app.car.infrastructure.mapper.toEntity
import initializer.backend.spring.app.car.infrastructure.repositories.CarModelRepository
import initializer.backend.spring.utils.Mode
import kotlinx.coroutines.flow.map
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
@Profile(Mode.DEV)
class CarModelService(
    private val repository: CarModelRepository
) {
    suspend fun create(request: CarModelRequest): CarModel {
        val existing = repository.findByBrandAndModel(request.brand, request.model)
        if (existing != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Ce modèle existe déjà.")
        }
        val entity = CarModel(
            brand = request.brand,
            model = request.model,
            generation = request.generation,
            bodyType = request.bodyType
        ).toEntity()
        return repository.save(entity).toDomain()
    }

    suspend fun findById(id: Long): CarModel {
        return repository.findById(id)?.toDomain()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Modèle introuvable.")
    }

    suspend fun findByBrand(brand: String) = repository.findByBrand(brand).map { it.toDomain() }
}
