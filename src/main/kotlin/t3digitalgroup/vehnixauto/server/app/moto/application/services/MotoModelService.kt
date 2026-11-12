package t3digitalgroup.vehnixauto.server.app.moto.application.services

import kotlinx.coroutines.flow.map
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.moto.domain.models.MotoModel
import t3digitalgroup.vehnixauto.server.app.moto.domain.models.request.MotoModelAdminUpdateRequest
import t3digitalgroup.vehnixauto.server.app.moto.domain.models.request.MotoModelRequest
import t3digitalgroup.vehnixauto.server.app.moto.infrastructure.entities.MotoModelEntity
import t3digitalgroup.vehnixauto.server.app.moto.infrastructure.mapper.toDomain
import t3digitalgroup.vehnixauto.server.app.moto.infrastructure.mapper.toEntity
import t3digitalgroup.vehnixauto.server.app.moto.infrastructure.repositories.MotoModelRepository
import t3digitalgroup.vehnixauto.server.utils.Mode

@Service
@Profile(Mode.DEV)
class MotoModelService(
    private val repository: MotoModelRepository,
) {
    suspend fun create(request: MotoModelRequest): MotoModel {
        val existing = repository.findByBrandAndModel(request.brand, request.model)
        if (existing != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Ce modèle existe déjà.")
        }
        val entity = MotoModel(
            brand = request.brand,
            model = request.model,
            generation = request.generation,
            bodyType = request.bodyType,
        ).toEntity()
        return repository.save(entity).toDomain()
    }

    suspend fun adminUpdate(id: Long, request: MotoModelAdminUpdateRequest): MotoModel {
        val existing = repository.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Modèle introuvable.")
        val brand = request.brand ?: existing.brand
        val model = request.model ?: existing.model
        if (brand != existing.brand || model != existing.model) {
            val conflict = repository.findByBrandAndModel(brand, model)
            if (conflict != null && conflict.motoModelId != id) {
                throw ResponseStatusException(HttpStatus.CONFLICT, "Ce modèle existe déjà.")
            }
        }
        val updated = MotoModelEntity(
            motoModelId = existing.motoModelId,
            brand = brand,
            model = model,
            generation = request.generation ?: existing.generation,
            bodyType = request.bodyType ?: existing.bodyType,
            createdAt = existing.createdAt,
        )
        return repository.save(updated).toDomain()
    }

    suspend fun findById(id: Long): MotoModel =
        repository.findById(id)?.toDomain()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Modèle introuvable.")

    suspend fun findByBrand(brand: String) = repository.findByBrand(brand).map { it.toDomain() }

    suspend fun findAll() = repository.findAll().map { it.toDomain() }
}
