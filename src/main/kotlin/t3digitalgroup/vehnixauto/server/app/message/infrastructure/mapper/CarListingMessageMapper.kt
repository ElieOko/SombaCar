package t3digitalgroup.vehnixauto.server.app.message.infrastructure.mapper

import t3digitalgroup.vehnixauto.server.app.message.domain.models.*
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.entities.*

fun CarListingThreadEntity.toDomain() = CarListingThread(
    threadId = this.threadId,
    carListingId = this.carListingId,
    buyerId = this.buyerId,
    sellerId = this.sellerId,
    status = this.status,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

fun CarListingThread.toEntity() = CarListingThreadEntity(
    threadId = this.threadId,
    carListingId = this.carListingId,
    buyerId = this.buyerId,
    sellerId = this.sellerId,
    status = this.status,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

fun CarListingMessageEntity.toDomain() = CarListingMessage(
    messageId = this.messageId,
    threadId = this.threadId,
    senderId = this.senderId,
    content = this.content,
    isRead = this.isRead,
    sentAt = this.sentAt,
)

fun CarListingMessage.toEntity() = CarListingMessageEntity(
    messageId = this.messageId,
    threadId = this.threadId,
    senderId = this.senderId,
    content = this.content,
    isRead = this.isRead,
    sentAt = this.sentAt,
)
