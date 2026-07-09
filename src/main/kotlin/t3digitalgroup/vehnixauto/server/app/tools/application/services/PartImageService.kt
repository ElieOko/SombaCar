package t3digitalgroup.vehnixauto.server.app.tools.application.services

import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import t3digitalgroup.vehnixauto.server.adaptater.provider.gcs.GcsService
import t3digitalgroup.vehnixauto.server.app.tools.infrastructure.entities.PartImageEntity
import t3digitalgroup.vehnixauto.server.app.tools.infrastructure.mapper.toDomain
import t3digitalgroup.vehnixauto.server.app.tools.infrastructure.repositories.PartImageRepository
import t3digitalgroup.vehnixauto.server.utils.Mode

@Service
@Profile(Mode.DEV)
class PartImageService(
    private val repository: PartImageRepository,
    private val gcsService: GcsService,
) {
    private val subdirectory = "car/part/"

    suspend fun createFromFile(partListingId: Long, file: MultipartFile): PartImageEntity {
        val imageUri = gcsService.uploadFile(file, subdirectory)
            ?: throw IllegalStateException("Failed to upload image")
        val data = PartImageEntity(
            partListingId = partListingId,
            name = file.originalFilename ?: file.name,
            path = imageUri,
        )
        return repository.save(data)
    }

    suspend fun findByPartListingIdIn(partListingIds: List<Long>) =
        repository.findByPartListingIdIn(partListingIds).toList().map { it.toDomain() }
}
