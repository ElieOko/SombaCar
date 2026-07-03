package t3digitalgroup.vehnixauto.server.app.message.application.services

import kotlinx.coroutines.flow.map
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
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
    private val messageRepository: MessageRepository
) {
    suspend fun sendUserMessage(request: SupportMessageRequest): Message {
        val thread = threadRepository.findById(request.threadId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable.")
        if (thread.status == SupportThreadStatus.CLOSED.name || thread.status == SupportThreadStatus.RESOLVED.name) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette demande est déjà clôturée.")
        }
        thread.updatedAt = LocalDateTime.now()
        threadRepository.save(thread)

        val entity = Message(
            threadId = request.threadId,
            senderType = MessageSenderType.USER.name,
            senderId = request.senderId,
            content = request.content
        ).toEntity()
        return messageRepository.save(entity).toDomain()
    }

    suspend fun replyAsPlatform(request: PlatformReplyRequest): Message {
        val thread = threadRepository.findById(request.threadId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable.")
        thread.status = SupportThreadStatus.IN_PROGRESS.name
        thread.updatedAt = LocalDateTime.now()
        threadRepository.save(thread)

        val entity = Message(
            threadId = request.threadId,
            senderType = MessageSenderType.PLATFORM.name,
            senderId = request.adminId,
            content = request.content
        ).toEntity()
        return messageRepository.save(entity).toDomain()
    }

    suspend fun findByThreadId(threadId: Long) = messageRepository.findByThreadId(threadId).map { it.toDomain() }

    suspend fun markAsRead(messageId: Long): Message {
        val entity = messageRepository.findById(messageId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Message introuvable.")
        entity.isRead = true
        return messageRepository.save(entity).toDomain()
    }
}
