package t3digitalgroup.vehnixauto.server.app.message.application.services

import kotlinx.coroutines.flow.*
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.car.infrastructure.repositories.CarListingRepository
import t3digitalgroup.vehnixauto.server.app.message.domain.models.*
import t3digitalgroup.vehnixauto.server.app.message.domain.models.request.*
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.mapper.*
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.repositories.*
import t3digitalgroup.vehnixauto.server.utils.ListingStatus
import t3digitalgroup.vehnixauto.server.utils.Mode
import java.time.LocalDateTime

@Service
@Profile(Mode.DEV)
class CarListingThreadService(
    private val threadRepository: CarListingThreadRepository,
    private val messageRepository: CarListingMessageRepository,
    private val carListingRepository: CarListingRepository,
) {
    suspend fun openThread(request: CarListingThreadRequest): CarListingThread {
        val listing = carListingRepository.findById(request.carListingId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Annonce introuvable.")
        if (listing.status != ListingStatus.ACTIVE.name) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette annonce n'est plus disponible.")
        }
        if (listing.userId == request.buyerId) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Vous ne pouvez pas contacter votre propre annonce.")
        }

        val existingThread = threadRepository.findByCarListingIdAndBuyerId(request.carListingId, request.buyerId)
        if (existingThread != null) {
            if (existingThread.status == CarListingThreadStatus.CLOSED.name) {
                existingThread.status = CarListingThreadStatus.OPEN.name
            }
            existingThread.updatedAt = LocalDateTime.now()
            val savedThread = threadRepository.save(existingThread)
            messageRepository.save(
                CarListingMessage(
                    threadId = savedThread.threadId!!,
                    senderId = request.buyerId,
                    content = request.initialMessage,
                ).toEntity()
            )
            return savedThread.toDomain()
        }

        val savedThread = threadRepository.save(
            CarListingThread(
                carListingId = request.carListingId,
                buyerId = request.buyerId,
                sellerId = listing.userId,
            ).toEntity()
        )
        messageRepository.save(
            CarListingMessage(
                threadId = savedThread.threadId!!,
                senderId = request.buyerId,
                content = request.initialMessage,
            ).toEntity()
        )
        return savedThread.toDomain()
    }

    suspend fun findById(threadId: Long): CarListingThread {
        return threadRepository.findById(threadId)?.toDomain()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation introuvable.")
    }

    suspend fun findByParticipantUserId(userId: Long) =
        threadRepository.findByParticipantUserId(userId).map { it.toDomain() }

    suspend fun findByCarListingId(carListingId: Long) =
        threadRepository.findByCarListingId(carListingId).map { it.toDomain() }

    suspend fun updateStatus(threadId: Long, status: CarListingThreadStatus): CarListingThread {
        val entity = threadRepository.findById(threadId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation introuvable.")
        entity.status = status.name
        entity.updatedAt = LocalDateTime.now()
        return threadRepository.save(entity).toDomain()
    }
}

@Service
@Profile(Mode.DEV)
class CarListingMessageService(
    private val threadRepository: CarListingThreadRepository,
    private val messageRepository: CarListingMessageRepository,
) {
    suspend fun sendMessage(request: CarListingMessageRequest): CarListingMessage {
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

        return messageRepository.save(
            CarListingMessage(
                threadId = request.threadId,
                senderId = request.senderId,
                content = request.content,
            ).toEntity()
        ).toDomain()
    }

    suspend fun findByThreadIdForUser(threadId: Long, userId: Long): Flow<CarListingMessage> {
        assertParticipant(threadId, userId)
        return messageRepository.findByThreadId(threadId).map { it.toDomain() }
    }

    private suspend fun assertParticipant(threadId: Long, userId: Long) {
        val thread = threadRepository.findById(threadId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation introuvable.")
        if (userId != thread.buyerId && userId != thread.sellerId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Vous ne participez pas à cette conversation.")
        }
    }

    suspend fun markAsRead(messageId: Long, userId: Long): CarListingMessage {
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
        return messageRepository.save(entity).toDomain()
    }
}
