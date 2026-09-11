package t3digitalgroup.vehnixauto.server.app.message.infrastructure.mapper

import t3digitalgroup.vehnixauto.server.app.message.domain.models.*
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.entities.*

fun MotoListingThreadEntity.toDomain() = MotoListingThread(
    threadId = this.threadId,
    motoListingId = this.motoListingId,
    buyerId = this.buyerId,
    sellerId = this.sellerId,
    status = this.status,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

fun MotoListingThread.toEntity() = MotoListingThreadEntity(
    threadId = this.threadId,
    motoListingId = this.motoListingId,
    buyerId = this.buyerId,
    sellerId = this.sellerId,
    status = this.status,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

fun MotoListingMessageEntity.toDomain(attachments: List<MessageAttachment> = emptyList()) = MotoListingMessage(
    messageId = this.messageId,
    threadId = this.threadId,
    senderId = this.senderId,
    content = this.content,
    isRead = this.isRead,
    sentAt = this.sentAt,
    attachments = attachments,
)

fun MotoListingMessage.toEntity() = MotoListingMessageEntity(
    messageId = this.messageId,
    threadId = this.threadId,
    senderId = this.senderId,
    content = this.content,
    isRead = this.isRead,
    sentAt = this.sentAt,
)

fun PartListingThreadEntity.toDomain() = PartListingThread(
    threadId = this.threadId,
    partListingId = this.partListingId,
    buyerId = this.buyerId,
    sellerId = this.sellerId,
    status = this.status,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

fun PartListingThread.toEntity() = PartListingThreadEntity(
    threadId = this.threadId,
    partListingId = this.partListingId,
    buyerId = this.buyerId,
    sellerId = this.sellerId,
    status = this.status,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

fun PartListingMessageEntity.toDomain(attachments: List<MessageAttachment> = emptyList()) = PartListingMessage(
    messageId = this.messageId,
    threadId = this.threadId,
    senderId = this.senderId,
    content = this.content,
    isRead = this.isRead,
    sentAt = this.sentAt,
    attachments = attachments,
)

fun PartListingMessage.toEntity() = PartListingMessageEntity(
    messageId = this.messageId,
    threadId = this.threadId,
    senderId = this.senderId,
    content = this.content,
    isRead = this.isRead,
    sentAt = this.sentAt,
)
