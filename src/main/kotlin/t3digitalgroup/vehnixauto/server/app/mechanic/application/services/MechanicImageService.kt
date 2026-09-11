package t3digitalgroup.vehnixauto.server.app.mechanic.application.services

import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import t3digitalgroup.vehnixauto.server.adaptater.provider.gcs.GcsService
import t3digitalgroup.vehnixauto.server.app.mechanic.infrastructure.entities.MechanicImageEntity
import t3digitalgroup.vehnixauto.server.app.mechanic.infrastructure.mapper.toDomain
import t3digitalgroup.vehnixauto.server.app.mechanic.infrastructure.repositories.MechanicImageRepository
import t3digitalgroup.vehnixauto.server.utils.Mode

@Service
@Profile(Mode.DEV)
class MechanicImageService(
    private val repository: MechanicImageRepository,
    private val gcsService: GcsService,
) {
    private val subdirectory = "mechanic/"

    suspend fun createFromFile(mechanicId: Long, file: MultipartFile): MechanicImageEntity {
        val imageUri = gcsService.uploadFile(file, subdirectory)
            ?: throw IllegalStateException("Failed to upload image")
        return repository.save(
            MechanicImageEntity(
                mechanicId = mechanicId,
                name = file.originalFilename ?: file.name,
                path = imageUri,
            ),
        )
    }

    suspend fun findByMechanicIdIn(mechanicIds: List<Long>) =
        repository.findByMechanicIdIn(mechanicIds).toList().map { it.toDomain() }
}
