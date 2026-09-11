package t3digitalgroup.vehnixauto.server.app.message.application.services

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.asFlow
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.message.domain.models.*
import t3digitalgroup.vehnixauto.server.app.message.domain.models.request.*
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.mapper.toDomain
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.mapper.toEntity
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.repositories.*
import t3digitalgroup.vehnixauto.server.utils.*
import java.time.*

@Service
@Profile(Mode.DEV)
class SupportThreadService(
    private val threadRepository: SupportThreadRepository,
    private val messageRepository: MessageRepository
) {
    suspend fun openThread(request: SupportThreadRequest): SupportThread {
        val threadEntity = SupportThread(
            userId = request.userId,
            subject = request.subject,
            partSought = request.partSought,
            partReference = request.partReference,
            compatibleBrand = request.compatibleBrand,
            compatibleModel = request.compatibleModel
        ).toEntity()

        val savedThread = threadRepository.save(threadEntity)
        messageRepository.save(
            Message(
                threadId = savedThread.threadId!!,
                senderType = MessageSenderType.USER.name,
                senderId = request.userId,
                content = request.initialMessage
            ).toEntity()
        )
        return savedThread.toDomain()
    }

    suspend fun findById(threadId: Long): SupportThread {
        return threadRepository.findById(threadId)?.toDomain()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable.")
    }

    suspend fun findByUserId(userId: Long) = threadRepository.findByUserId(userId).map { it.toDomain() }

    suspend fun findOpenThreads() = threadRepository.findByStatus(SupportThreadStatus.OPEN.name).map { it.toDomain() }

    suspend fun updateStatus(threadId: Long, status: SupportThreadStatus): SupportThread {
        val entity = threadRepository.findById(threadId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable.")
        entity.status = status.name
        entity.updatedAt = LocalDateTime.now()
        return threadRepository.save(entity).toDomain()
    }
}

@Service
@Profile(Mode.DEV)
class MessageService(
    private val threadRepository: SupportThreadRepository,
    private val messageRepository: MessageRepository,
    private val attachmentService: MessageAttachmentService,
) {
    suspend fun sendUserMessage(
        request: SupportMessageRequest,
        files: List<MultipartFile> = emptyList(),
    ): Message {
        validateMessagePayload(request.content, files)
        val thread = threadRepository.findById(request.threadId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable.")
        if (thread.status == SupportThreadStatus.CLOSED.name || thread.status == SupportThreadStatus.RESOLVED.name) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette demande est déjà clôturée.")
        }
        thread.updatedAt = LocalDateTime.now()
        threadRepository.save(thread)

        val saved = messageRepository.save(
            Message(
                threadId = request.threadId,
                senderType = MessageSenderType.USER.name,
                senderId = request.senderId,
                content = request.content?.trim()?.takeIf { it.isNotEmpty() },
            ).toEntity()
        )
        val attachments = saved.messageId?.let { messageId ->
            attachmentService.saveSupportAttachments(messageId, files)
        }.orEmpty()
        return saved.toDomain(attachments)
    }

    suspend fun replyAsPlatform(
        request: PlatformReplyRequest,
        files: List<MultipartFile> = emptyList(),
    ): Message {
        validateMessagePayload(request.content, files)
        val thread = threadRepository.findById(request.threadId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable.")
        thread.status = SupportThreadStatus.IN_PROGRESS.name
        thread.updatedAt = LocalDateTime.now()
        threadRepository.save(thread)

        val saved = messageRepository.save(
            Message(
                threadId = request.threadId,
                senderType = MessageSenderType.PLATFORM.name,
                senderId = request.adminId,
                content = request.content?.trim()?.takeIf { it.isNotEmpty() },
            ).toEntity()
        )
        val attachments = saved.messageId?.let { messageId ->
            attachmentService.saveSupportAttachments(messageId, files)
        }.orEmpty()
        return saved.toDomain(attachments)
    }

    suspend fun findByThreadId(threadId: Long): Flow<Message> {
        val messages = messageRepository.findByThreadId(threadId).toList()
        val attachmentsByMessageId = attachmentService
            .findSupportAttachments(messages.mapNotNull { it.messageId })
            .groupBy { it.messageId }
        return messages.map { message ->
            message.toDomain(attachmentsByMessageId[message.messageId].orEmpty())
        }.asFlow()
    }

    suspend fun markAsRead(messageId: Long): Message {
        val entity = messageRepository.findById(messageId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Message introuvable.")
        entity.isRead = true
        return messageRepository.save(entity).toDomain(
            attachmentService.findSupportAttachments(listOfNotNull(entity.messageId)),
        )
    }

    private fun validateMessagePayload(content: String?, files: List<MultipartFile>) {
        val hasContent = !content.isNullOrBlank()
        val hasFiles = files.any { !it.isEmpty }
        if (!hasContent && !hasFiles) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Le message doit contenir du texte ou au moins une photo.",
            )
        }
    }
}
