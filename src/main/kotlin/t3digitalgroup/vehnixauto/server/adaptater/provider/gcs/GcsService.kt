package t3digitalgroup.vehnixauto.server.adaptater.provider.gcs

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class GcsService(
    private val storage: Storage,
    @Value("\${gcs.bucket-name}") private val bucketName: String,
    @Value("\${gcs.subdirectory}") private val rootSubdirectory: String,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun uploadFile(file: MultipartFile, directory: String = ""): String? {
        val fileName = "$rootSubdirectory/$directory${file.originalFilename}"
        val blob = storage.create(
            BlobInfo.newBuilder(bucketName, fileName).build(),
            file.bytes
        )
        return blob.mediaLink
    }

    suspend fun deleteFile(fileName: String?, directory: String = ""): Boolean? = coroutineScope {
        try {
            val blobId = BlobId.of(bucketName, "$rootSubdirectory/$directory$fileName")
            storage.delete(blobId)
        } catch (e: Exception) {
            log.info("GCS delete failed: ${e.message}")
            null
        }
    }
}
