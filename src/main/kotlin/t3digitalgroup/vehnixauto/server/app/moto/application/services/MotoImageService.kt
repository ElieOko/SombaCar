package t3digitalgroup.vehnixauto.server.app.moto.application.services

import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import t3digitalgroup.vehnixauto.server.adaptater.provider.gcs.GcsService
import t3digitalgroup.vehnixauto.server.app.moto.infrastructure.entities.MotoImageEntity
import t3digitalgroup.vehnixauto.server.app.moto.infrastructure.mapper.toDomain
import t3digitalgroup.vehnixauto.server.app.moto.infrastructure.repositories.MotoImageRepository
import t3digitalgroup.vehnixauto.server.utils.Mode

@Service
@Profile(Mode.DEV)
class MotoImageService(
    private val repository: MotoImageRepository,
    private val gcsService: GcsService,
) {
    private val subdirectory = "car/moto"

    suspend fun createFromFile(motoId: Long, file: MultipartFile): MotoImageEntity {
        val imageUri = gcsService.uploadFile(file, subdirectory)
            ?: throw IllegalStateException("Failed to upload image")
        val data = MotoImageEntity(
            motoId = motoId,
            name = file.originalFilename ?: file.name,
            path = imageUri,
        )
        return repository.save(data)
    }

    suspend fun findByMotoIdIn(motoIds: List<Long>) =
        repository.findByMotoIdIn(motoIds).toList().map { it.toDomain() }
}
