package t3digitalgroup.vehnixauto.server.app.notification.infrastructure.mapper

import t3digitalgroup.vehnixauto.server.app.notification.domain.models.*
import t3digitalgroup.vehnixauto.server.app.notification.infrastructure.entities.*


fun NotificationEntity.toDomain() = Notification(
    id = this.id,
    userId = this.userId,
    title = this.title,
    message = this.message,
    tag = this.tag,
    isActive = this.isActive,
    timestamp = this.created,
    user = this.user
)

fun Notification.toEntity() = NotificationEntity(
    id = this.id,
    userId = this.userId,
    title = this.title,
    isActive = this.isActive,
    message = this.message,
    tag = this.tag,
)

fun NotificationSystemEntity.toDomain() = NotificationSystem(
    notificationSystemId = this.id,
    title = this.title,
    description = this.description,
    dateCreated = this.dateCreated
)

fun NotificationSystem.toEntity() = NotificationSystemEntity(
    id = this.notificationSystemId,
    title = this.title,
    description = this.description,
    dateCreated = this.dateCreated
)
