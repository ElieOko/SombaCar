package initializer.backend.spring.app.notification.domain.models

import initializer.backend.spring.app.user.domain.models.UserDto
import java.time.LocalDate
import java.time.LocalDateTime

data class NotificationSystem(
    val notificationSystemId: Long? = null,
    val title: String,
    val description: String,
    val dateCreated: LocalDate = LocalDate.now()
)

data class Notification(
    val id: Long? = null,
    val userId: Long? = null,
    val title: String,
    val isActive: Boolean = true,
    val message: String,
    val tag: String = TagType.PART_INQUIRY.toString(),
    val timestamp: LocalDateTime = LocalDateTime.now(),
    var user: UserDto? = null
)
