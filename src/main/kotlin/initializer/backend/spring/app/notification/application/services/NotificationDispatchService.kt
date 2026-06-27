package initializer.backend.spring.app.notification.application.services

import initializer.backend.spring.app.notification.domain.models.Notification
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class NotificationDispatchService(
    private val messagingTemplate: SimpMessagingTemplate
) {
    fun sendNotificationAll(message: Notification) {
        messagingTemplate.convertAndSend("/topic/notifications", message)
    }

    fun sendNotificationToUser(username: String, message: Notification) {
        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", message)
    }
}
