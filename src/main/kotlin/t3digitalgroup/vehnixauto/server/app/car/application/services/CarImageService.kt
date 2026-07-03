package t3digitalgroup.vehnixauto.server.app.car.application.services

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import t3digitalgroup.vehnixauto.server.adaptater.provider.gcs.GcsService
import t3digitalgroup.vehnixauto.server.app.car.domain.models.*
import t3digitalgroup.vehnixauto.server.app.car.domain.models.request.*
import t3digitalgroup.vehnixauto.server.app.car.infrastructure.entities.*
import t3digitalgroup.vehnixauto.server.app.car.infrastructure.repositories.*
import t3digitalgroup.vehnixauto.server.utils.*

@Service
@Profile(Mode.DEV)
class CarImageService(
    private val repository: CarImageRepository,
    private val gcsService: GcsService,
) {
    private val subdirectory = "car/"

    suspend fun create(image: CarImage): CarImageEntity {
        val file = base64ToMultipartFile(image.name, "car")
        return createFromFile(image.carId!!, file)
    }

    suspend fun createFromFile(carId: Long, file: MultipartFile): CarImageEntity {
        val imageUri = gcsService.uploadFile(file, subdirectory)
            ?: throw IllegalStateException("Failed to upload image")
        val data = CarImageEntity(
            carId = carId,
            name = file.originalFilename ?: file.name,
            path = imageUri,
        )
        return repository.save(data)
    }

    suspend fun findCarIdIn(ids: List<Long>) = repository.findByCarIdIn(ids)

    suspend fun updateFile(carId: Long, images: List<ImageChangeRequest?>) = coroutineScope {
        val stored = findCarIdIn(listOf(carId)).toList()
        val imagesByCar = stored.groupBy { it.carId }
        var state = false
        images.forEach { change ->
            val existing = imagesByCar[carId]?.firstOrNull { it.name == change?.old }
            if (existing != null) {
                val deleted = gcsService.deleteFile(existing.name, subdirectory)
                if (deleted == true && change?.name != null) {
                    val file = base64ToMultipartFile(change.name, "car")
                    val imageUri = gcsService.uploadFile(file, subdirectory)
                    existing.path = imageUri!!
                    existing.name = file.originalFilename!!
                    repository.save(existing)
                    state = true
                }
            }
        }
        state
    }

    suspend fun deleteFile(carId: Long, images: List<ImageRequest?>) = coroutineScope {
        val stored = findCarIdIn(listOf(carId)).toList()
        val imagesByCar = stored.groupBy { it.carId }
        var state = false
        images.forEach { request ->
            val existing = imagesByCar[carId]?.firstOrNull { it.name == request?.name }
            if (existing != null) {
                val deleted = gcsService.deleteFile(existing.name, subdirectory)
                if (deleted == true) {
                    repository.delete(existing)
                    state = true
                }
            }
        }
        state
    }
}
