package t3digitalgroup.vehnixauto.server.app.car.application.services

import kotlinx.coroutines.flow.map
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.car.domain.models.*
import t3digitalgroup.vehnixauto.server.app.car.domain.models.request.*
import t3digitalgroup.vehnixauto.server.app.car.infrastructure.mapper.*
import t3digitalgroup.vehnixauto.server.app.car.infrastructure.repositories.CarModelRepository
import t3digitalgroup.vehnixauto.server.utils.Mode

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

    suspend fun findAll() = repository.findAll().map { it.toDomain() }
}
