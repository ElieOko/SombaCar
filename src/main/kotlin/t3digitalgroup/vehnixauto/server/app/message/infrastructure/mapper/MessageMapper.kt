package t3digitalgroup.vehnixauto.server.app.message.infrastructure.mapper

import t3digitalgroup.vehnixauto.server.app.message.domain.models.*
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.entities.*


fun SupportThreadEntity.toDomain() = SupportThread(
    threadId = this.threadId,
    userId = this.userId,
    subject = this.subject,
    partSought = this.partSought,
    partReference = this.partReference,
    compatibleBrand = this.compatibleBrand,
    compatibleModel = this.compatibleModel,
    status = this.status,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

fun SupportThread.toEntity() = SupportThreadEntity(
    threadId = this.threadId,
    userId = this.userId,
    subject = this.subject,
    partSought = this.partSought,
    partReference = this.partReference,
    compatibleBrand = this.compatibleBrand,
    compatibleModel = this.compatibleModel,
    status = this.status,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

fun MessageEntity.toDomain() = Message(
    messageId = this.messageId,
    threadId = this.threadId,
    senderType = this.senderType,
    senderId = this.senderId,
    content = this.content,
    isRead = this.isRead,
    sentAt = this.sentAt
)

fun Message.toEntity() = MessageEntity(
    messageId = this.messageId,
    threadId = this.threadId,
    senderType = this.senderType,
    senderId = this.senderId,
    content = this.content,
    isRead = this.isRead,
    sentAt = this.sentAt
)
