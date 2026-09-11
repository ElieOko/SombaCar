package t3digitalgroup.vehnixauto.server.app.garage.application.services

import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import t3digitalgroup.vehnixauto.server.adaptater.provider.gcs.GcsService
import t3digitalgroup.vehnixauto.server.app.garage.infrastructure.entities.GarageImageEntity
import t3digitalgroup.vehnixauto.server.app.garage.infrastructure.mapper.toDomain
import t3digitalgroup.vehnixauto.server.app.garage.infrastructure.repositories.GarageImageRepository
import t3digitalgroup.vehnixauto.server.utils.Mode

@Service
@Profile(Mode.DEV)
class GarageImageService(
    private val repository: GarageImageRepository,
    private val gcsService: GcsService,
) {
    private val subdirectory = "garage/"

    suspend fun createFromFile(garageId: Long, file: MultipartFile): GarageImageEntity {
        val imageUri = gcsService.uploadFile(file, subdirectory)
            ?: throw IllegalStateException("Failed to upload image")
        return repository.save(
            GarageImageEntity(
                garageId = garageId,
                name = file.originalFilename ?: file.name,
                path = imageUri,
            ),
        )
    }

    suspend fun findByGarageIdIn(garageIds: List<Long>) =
        repository.findByGarageIdIn(garageIds).toList().map { it.toDomain() }
}
