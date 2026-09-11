package t3digitalgroup.vehnixauto.server.app.message.application.services

import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import t3digitalgroup.vehnixauto.server.adaptater.provider.gcs.GcsService
import t3digitalgroup.vehnixauto.server.app.message.domain.models.MessageAttachment
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.entities.CarListingMessageAttachmentEntity
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.entities.MotoListingMessageAttachmentEntity
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.entities.PartListingMessageAttachmentEntity
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.entities.SupportMessageAttachmentEntity
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.repositories.CarListingMessageAttachmentRepository
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.repositories.MotoListingMessageAttachmentRepository
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.repositories.PartListingMessageAttachmentRepository
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.repositories.SupportMessageAttachmentRepository
import t3digitalgroup.vehnixauto.server.utils.Mode

@Service
@Profile(Mode.DEV)
class MessageAttachmentService(
    private val carListingAttachmentRepository: CarListingMessageAttachmentRepository,
    private val motoListingAttachmentRepository: MotoListingMessageAttachmentRepository,
    private val partListingAttachmentRepository: PartListingMessageAttachmentRepository,
    private val supportAttachmentRepository: SupportMessageAttachmentRepository,
    private val gcsService: GcsService,
) {
    private val carListingSubdirectory = "messages/car-listings/"
    private val motoListingSubdirectory = "messages/moto-listings/"
    private val partListingSubdirectory = "messages/part-listings/"
    private val supportSubdirectory = "messages/support/"

    suspend fun saveCarListingAttachments(
        messageId: Long,
        files: List<MultipartFile>,
    ): List<MessageAttachment> =
        files.filter { !it.isEmpty }.map { file ->
            val fileUri = gcsService.uploadFile(file, carListingSubdirectory)
                ?: throw IllegalStateException("Échec de l'upload du fichier")
            carListingAttachmentRepository.save(
                CarListingMessageAttachmentEntity(
                    messageId = messageId,
                    name = file.originalFilename ?: file.name,
                    path = fileUri,
                    mimeType = file.contentType,
                )
            ).toDomain()
        }

    suspend fun saveSupportAttachments(
        messageId: Long,
        files: List<MultipartFile>,
    ): List<MessageAttachment> =
        files.filter { !it.isEmpty }.map { file ->
            val fileUri = gcsService.uploadFile(file, supportSubdirectory)
                ?: throw IllegalStateException("Échec de l'upload du fichier")
            supportAttachmentRepository.save(
                SupportMessageAttachmentEntity(
                    messageId = messageId,
                    name = file.originalFilename ?: file.name,
                    path = fileUri,
                    mimeType = file.contentType,
                )
            ).toDomain()
        }

    suspend fun findCarListingAttachments(messageIds: List<Long>): List<MessageAttachment> {
        if (messageIds.isEmpty()) return emptyList()
        return carListingAttachmentRepository.findByMessageIdIn(messageIds)
            .toList()
            .map { it.toDomain() }
    }

    suspend fun saveMotoListingAttachments(
        messageId: Long,
        files: List<MultipartFile>,
    ): List<MessageAttachment> =
        files.filter { !it.isEmpty }.map { file ->
            val fileUri = gcsService.uploadFile(file, motoListingSubdirectory)
                ?: throw IllegalStateException("Échec de l'upload du fichier")
            motoListingAttachmentRepository.save(
                MotoListingMessageAttachmentEntity(
                    messageId = messageId,
                    name = file.originalFilename ?: file.name,
                    path = fileUri,
                    mimeType = file.contentType,
                )
            ).toDomain()
        }

    suspend fun savePartListingAttachments(
        messageId: Long,
        files: List<MultipartFile>,
    ): List<MessageAttachment> =
        files.filter { !it.isEmpty }.map { file ->
            val fileUri = gcsService.uploadFile(file, partListingSubdirectory)
                ?: throw IllegalStateException("Échec de l'upload du fichier")
            partListingAttachmentRepository.save(
                PartListingMessageAttachmentEntity(
                    messageId = messageId,
                    name = file.originalFilename ?: file.name,
                    path = fileUri,
                    mimeType = file.contentType,
                )
            ).toDomain()
        }

    suspend fun findMotoListingAttachments(messageIds: List<Long>): List<MessageAttachment> {
        if (messageIds.isEmpty()) return emptyList()
        return motoListingAttachmentRepository.findByMessageIdIn(messageIds)
            .toList()
            .map { it.toDomain() }
    }

    suspend fun findPartListingAttachments(messageIds: List<Long>): List<MessageAttachment> {
        if (messageIds.isEmpty()) return emptyList()
        return partListingAttachmentRepository.findByMessageIdIn(messageIds)
            .toList()
            .map { it.toDomain() }
    }

    suspend fun findSupportAttachments(messageIds: List<Long>): List<MessageAttachment> {
        if (messageIds.isEmpty()) return emptyList()
        return supportAttachmentRepository.findByMessageIdIn(messageIds)
            .toList()
            .map { it.toDomain() }
    }

    private fun CarListingMessageAttachmentEntity.toDomain() = MessageAttachment(
        attachmentId = attachmentId,
        messageId = messageId,
        name = name,
        path = path,
        mimeType = mimeType,
    )

    private fun MotoListingMessageAttachmentEntity.toDomain() = MessageAttachment(
        attachmentId = attachmentId,
        messageId = messageId,
        name = name,
        path = path,
        mimeType = mimeType,
    )

    private fun PartListingMessageAttachmentEntity.toDomain() = MessageAttachment(
        attachmentId = attachmentId,
        messageId = messageId,
        name = name,
        path = path,
        mimeType = mimeType,
    )

    private fun SupportMessageAttachmentEntity.toDomain() = MessageAttachment(
        attachmentId = attachmentId,
        messageId = messageId,
        name = name,
        path = path,
        mimeType = mimeType,
    )
}
