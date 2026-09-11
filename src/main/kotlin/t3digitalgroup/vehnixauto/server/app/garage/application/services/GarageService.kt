package t3digitalgroup.vehnixauto.server.app.garage.application.services

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.garage.domain.models.Garage
import t3digitalgroup.vehnixauto.server.app.garage.domain.models.request.GarageRequest
import t3digitalgroup.vehnixauto.server.app.garage.domain.models.request.GarageUpdateRequest
import t3digitalgroup.vehnixauto.server.app.garage.infrastructure.mapper.toDomain
import t3digitalgroup.vehnixauto.server.app.garage.infrastructure.mapper.toEntity
import t3digitalgroup.vehnixauto.server.app.garage.infrastructure.repositories.GarageRepository
import t3digitalgroup.vehnixauto.server.utils.GarageType
import t3digitalgroup.vehnixauto.server.utils.GeoCoordinatesRequest
import t3digitalgroup.vehnixauto.server.utils.Mode
import t3digitalgroup.vehnixauto.server.utils.validateGeoCoordinates
import java.time.LocalDateTime

@Service
@Profile(Mode.DEV)
class GarageService(
    private val repository: GarageRepository,
    private val garageImageService: GarageImageService,
) {
    suspend fun create(userId: Long, request: GarageRequest): Garage {
        validateGeoCoordinates(request.latitude, request.longitude)
        val saved = repository.save(
            Garage(
                userId = userId,
                name = request.name,
                description = request.description,
                address = request.address,
                city = request.city,
                country = request.country,
                phone = request.phone,
                latitude = request.latitude,
                longitude = request.longitude,
                garageType = request.garageType.name,
            ).toEntity(),
        ).toDomain()
        return enrichGarages(listOf(saved)).first()
    }

    suspend fun addImages(userId: Long, garageId: Long, files: List<MultipartFile>): Garage {
        requireOwnedGarage(userId, garageId)
        files.filter { !it.isEmpty }.forEach { file ->
            garageImageService.createFromFile(garageId, file)
        }
        return findById(garageId)
    }

    suspend fun findById(id: Long): Garage =
        repository.findById(id)?.toDomain()?.let { enrichGarages(listOf(it)).first() }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Garage introuvable.")

    suspend fun findActiveByUserId(userId: Long): List<Garage> =
        enrichGarages(repository.findActiveByUserId(userId).map { it.toDomain() }.toList())

    suspend fun findAllByUserId(userId: Long): List<Garage> =
        enrichGarages(repository.findAllByUserId(userId).map { it.toDomain() }.toList())

    suspend fun findAllActive(): List<Garage> =
        enrichGarages(repository.findAllActive().map { it.toDomain() }.toList())

    suspend fun findActiveByType(garageType: GarageType): List<Garage> =
        enrichGarages(repository.findActiveByType(garageType.name).map { it.toDomain() }.toList())

    suspend fun update(userId: Long, garageId: Long, request: GarageUpdateRequest): Garage {
        validateGeoCoordinates(request.latitude, request.longitude)
        val entity = requireOwnedGarage(userId, garageId)
        entity.name = request.name
        entity.description = request.description
        entity.address = request.address
        entity.city = request.city
        entity.country = request.country
        entity.phone = request.phone
        entity.latitude = request.latitude
        entity.longitude = request.longitude
        entity.garageType = request.garageType.name
        entity.updatedAt = LocalDateTime.now()
        return enrichGarages(listOf(repository.save(entity).toDomain())).first()
    }

    suspend fun updateCoordinates(userId: Long, garageId: Long, request: GeoCoordinatesRequest): Garage {
        val entity = requireOwnedGarage(userId, garageId)
        entity.latitude = request.latitude
        entity.longitude = request.longitude
        entity.updatedAt = LocalDateTime.now()
        return enrichGarages(listOf(repository.save(entity).toDomain())).first()
    }

    suspend fun deactivate(userId: Long, garageId: Long): Garage {
        val entity = requireOwnedGarage(userId, garageId)
        entity.isActive = false
        entity.updatedAt = LocalDateTime.now()
        return enrichGarages(listOf(repository.save(entity).toDomain())).first()
    }

    private suspend fun enrichGarages(garages: List<Garage>): List<Garage> {
        if (garages.isEmpty()) return garages
        val ids = garages.mapNotNull { it.garageId }
        if (ids.isEmpty()) return garages

        val imagesByGarageId = garageImageService.findByGarageIdIn(ids)
            .groupBy { it.garageId }

        return garages.map { garage ->
            garage.copy(images = imagesByGarageId[garage.garageId].orEmpty())
        }
    }

    private suspend fun requireOwnedGarage(userId: Long, garageId: Long) =
        repository.findById(garageId)?.also { garage ->
            if (garage.userId != userId) {
                throw ResponseStatusException(HttpStatus.FORBIDDEN, "Accès non autorisé.")
            }
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Garage introuvable.")
}
