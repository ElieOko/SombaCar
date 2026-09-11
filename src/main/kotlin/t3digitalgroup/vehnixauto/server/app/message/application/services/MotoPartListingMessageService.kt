package t3digitalgroup.vehnixauto.server.app.message.application.services

import kotlinx.coroutines.flow.*
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.message.domain.models.*
import t3digitalgroup.vehnixauto.server.app.message.domain.models.request.*
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.mapper.*
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.repositories.*
import t3digitalgroup.vehnixauto.server.app.moto.infrastructure.repositories.MotoListingRepository
import t3digitalgroup.vehnixauto.server.app.tools.infrastructure.repositories.PartListingRepository
import t3digitalgroup.vehnixauto.server.utils.ListingStatus
import t3digitalgroup.vehnixauto.server.utils.Mode
import java.time.LocalDateTime

@Service
@Profile(Mode.DEV)
class MotoListingThreadService(
    private val threadRepository: MotoListingThreadRepository,
    private val messageRepository: MotoListingMessageRepository,
    private val motoListingRepository: MotoListingRepository,
) {
    suspend fun openThread(request: MotoListingThreadRequest): MotoListingThread {
        val listing = motoListingRepository.findById(request.motoListingId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Annonce introuvable.")
        if (listing.status != ListingStatus.ACTIVE.name) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette annonce n'est plus disponible.")
        }
        if (listing.userId == request.buyerId) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Vous ne pouvez pas contacter votre propre annonce.")
        }

        val existingThread = threadRepository.findByMotoListingIdAndBuyerId(request.motoListingId, request.buyerId)
        if (existingThread != null) {
            if (existingThread.status == CarListingThreadStatus.CLOSED.name) {
                existingThread.status = CarListingThreadStatus.OPEN.name
            }
            existingThread.updatedAt = LocalDateTime.now()
            val savedThread = threadRepository.save(existingThread)
            messageRepository.save(
                MotoListingMessage(
                    threadId = savedThread.threadId!!,
                    senderId = request.buyerId,
                    content = request.initialMessage,
                ).toEntity()
            )
            return savedThread.toDomain()
        }

        val savedThread = threadRepository.save(
            MotoListingThread(
                motoListingId = request.motoListingId,
                buyerId = request.buyerId,
                sellerId = listing.userId,
            ).toEntity()
        )
        messageRepository.save(
            MotoListingMessage(
                threadId = savedThread.threadId!!,
                senderId = request.buyerId,
                content = request.initialMessage,
            ).toEntity()
        )
        return savedThread.toDomain()
    }

    suspend fun findById(threadId: Long): MotoListingThread =
        threadRepository.findById(threadId)?.toDomain()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation introuvable.")

    suspend fun findByParticipantUserId(userId: Long) =
        threadRepository.findByParticipantUserId(userId).map { it.toDomain() }

    suspend fun findByMotoListingId(motoListingId: Long) =
        threadRepository.findByMotoListingId(motoListingId).map { it.toDomain() }

    suspend fun updateStatus(threadId: Long, status: CarListingThreadStatus): MotoListingThread {
        val entity = threadRepository.findById(threadId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation introuvable.")
        entity.status = status.name
        entity.updatedAt = LocalDateTime.now()
        return threadRepository.save(entity).toDomain()
    }
}

@Service
@Profile(Mode.DEV)
class MotoListingMessageService(
    private val threadRepository: MotoListingThreadRepository,
    private val messageRepository: MotoListingMessageRepository,
    private val attachmentService: MessageAttachmentService,
) {
    suspend fun sendMessage(
        request: MotoListingMessageRequest,
        files: List<MultipartFile> = emptyList(),
    ): MotoListingMessage {
        validateMessagePayload(request.content, files)
        val thread = threadRepository.findById(request.threadId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation introuvable.")
        if (thread.status == CarListingThreadStatus.CLOSED.name) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette conversation est clôturée.")
        }
        if (request.senderId != thread.buyerId && request.senderId != thread.sellerId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Vous ne participez pas à cette conversation.")
        }

        thread.updatedAt = LocalDateTime.now()
        threadRepository.save(thread)

        val saved = messageRepository.save(
            MotoListingMessage(
                threadId = request.threadId,
                senderId = request.senderId,
                content = request.content?.trim()?.takeIf { it.isNotEmpty() },
            ).toEntity()
        )
        val attachments = saved.messageId?.let { messageId ->
            attachmentService.saveMotoListingAttachments(messageId, files)
        }.orEmpty()
        return saved.toDomain(attachments)
    }

    suspend fun findByThreadIdForUser(threadId: Long, userId: Long): Flow<MotoListingMessage> {
        assertParticipant(threadId, userId)
        val messages = messageRepository.findByThreadId(threadId).toList()
        val attachmentsByMessageId = attachmentService
            .findMotoListingAttachments(messages.mapNotNull { it.messageId })
            .groupBy { it.messageId }
        return messages.map { message ->
            message.toDomain(attachmentsByMessageId[message.messageId].orEmpty())
        }.asFlow()
    }

    suspend fun markAsRead(messageId: Long, userId: Long): MotoListingMessage {
        val entity = messageRepository.findById(messageId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Message introuvable.")
        val thread = threadRepository.findById(entity.threadId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation introuvable.")
        if (userId != thread.buyerId && userId != thread.sellerId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Vous ne participez pas à cette conversation.")
        }
        if (entity.senderId == userId) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Vous ne pouvez pas marquer vos propres messages comme lus.")
        }
        entity.isRead = true
        return messageRepository.save(entity).toDomain(
            attachmentService.findMotoListingAttachments(listOfNotNull(entity.messageId)),
        )
    }

    private suspend fun assertParticipant(threadId: Long, userId: Long) {
        val thread = threadRepository.findById(threadId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation introuvable.")
        if (userId != thread.buyerId && userId != thread.sellerId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Vous ne participez pas à cette conversation.")
        }
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

@Service
@Profile(Mode.DEV)
class PartListingThreadService(
    private val threadRepository: PartListingThreadRepository,
    private val messageRepository: PartListingMessageRepository,
    private val partListingRepository: PartListingRepository,
) {
    suspend fun openThread(request: PartListingThreadRequest): PartListingThread {
        val listing = partListingRepository.findById(request.partListingId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Annonce introuvable.")
        if (listing.status != ListingStatus.ACTIVE.name) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette annonce n'est plus disponible.")
        }
        if (listing.userId == request.buyerId) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Vous ne pouvez pas contacter votre propre annonce.")
        }

        val existingThread = threadRepository.findByPartListingIdAndBuyerId(request.partListingId, request.buyerId)
        if (existingThread != null) {
            if (existingThread.status == CarListingThreadStatus.CLOSED.name) {
                existingThread.status = CarListingThreadStatus.OPEN.name
            }
            existingThread.updatedAt = LocalDateTime.now()
            val savedThread = threadRepository.save(existingThread)
            messageRepository.save(
                PartListingMessage(
                    threadId = savedThread.threadId!!,
                    senderId = request.buyerId,
                    content = request.initialMessage,
                ).toEntity()
            )
            return savedThread.toDomain()
        }

        val savedThread = threadRepository.save(
            PartListingThread(
                partListingId = request.partListingId,
                buyerId = request.buyerId,
                sellerId = listing.userId,
            ).toEntity()
        )
        messageRepository.save(
            PartListingMessage(
                threadId = savedThread.threadId!!,
                senderId = request.buyerId,
                content = request.initialMessage,
            ).toEntity()
        )
        return savedThread.toDomain()
    }

    suspend fun findById(threadId: Long): PartListingThread =
        threadRepository.findById(threadId)?.toDomain()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation introuvable.")

    suspend fun findByParticipantUserId(userId: Long) =
        threadRepository.findByParticipantUserId(userId).map { it.toDomain() }

    suspend fun findByPartListingId(partListingId: Long) =
        threadRepository.findByPartListingId(partListingId).map { it.toDomain() }

    suspend fun updateStatus(threadId: Long, status: CarListingThreadStatus): PartListingThread {
        val entity = threadRepository.findById(threadId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation introuvable.")
        entity.status = status.name
        entity.updatedAt = LocalDateTime.now()
        return threadRepository.save(entity).toDomain()
    }
}

@Service
@Profile(Mode.DEV)
class PartListingMessageService(
    private val threadRepository: PartListingThreadRepository,
    private val messageRepository: PartListingMessageRepository,
    private val attachmentService: MessageAttachmentService,
) {
    suspend fun sendMessage(
        request: PartListingMessageRequest,
        files: List<MultipartFile> = emptyList(),
    ): PartListingMessage {
        validateMessagePayload(request.content, files)
        val thread = threadRepository.findById(request.threadId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation introuvable.")
        if (thread.status == CarListingThreadStatus.CLOSED.name) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette conversation est clôturée.")
        }
        if (request.senderId != thread.buyerId && request.senderId != thread.sellerId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Vous ne participez pas à cette conversation.")
        }

        thread.updatedAt = LocalDateTime.now()
        threadRepository.save(thread)

        val saved = messageRepository.save(
            PartListingMessage(
                threadId = request.threadId,
                senderId = request.senderId,
                content = request.content?.trim()?.takeIf { it.isNotEmpty() },
            ).toEntity()
        )
        val attachments = saved.messageId?.let { messageId ->
            attachmentService.savePartListingAttachments(messageId, files)
        }.orEmpty()
        return saved.toDomain(attachments)
    }

    suspend fun findByThreadIdForUser(threadId: Long, userId: Long): Flow<PartListingMessage> {
        assertParticipant(threadId, userId)
        val messages = messageRepository.findByThreadId(threadId).toList()
        val attachmentsByMessageId = attachmentService
            .findPartListingAttachments(messages.mapNotNull { it.messageId })
            .groupBy { it.messageId }
        return messages.map { message ->
            message.toDomain(attachmentsByMessageId[message.messageId].orEmpty())
        }.asFlow()
    }

    suspend fun markAsRead(messageId: Long, userId: Long): PartListingMessage {
        val entity = messageRepository.findById(messageId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Message introuvable.")
        val thread = threadRepository.findById(entity.threadId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation introuvable.")
        if (userId != thread.buyerId && userId != thread.sellerId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Vous ne participez pas à cette conversation.")
        }
        if (entity.senderId == userId) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Vous ne pouvez pas marquer vos propres messages comme lus.")
        }
        entity.isRead = true
        return messageRepository.save(entity).toDomain(
            attachmentService.findPartListingAttachments(listOfNotNull(entity.messageId)),
        )
    }

    private suspend fun assertParticipant(threadId: Long, userId: Long) {
        val thread = threadRepository.findById(threadId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation introuvable.")
        if (userId != thread.buyerId && userId != thread.sellerId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Vous ne participez pas à cette conversation.")
        }
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
