package t3digitalgroup.vehnixauto.server.app.notification.application.services

import org.springframework.stereotype.Service
import t3digitalgroup.vehnixauto.server.app.notification.domain.models.Notification

@Service
class NotificationDispatchService(
//    private val messagingTemplate: SimpMessagingTemplate
) {
    fun sendNotificationAll(message: Notification) {
//        messagingTemplate.convertAndSend("/topic/notifications", message)
    }

    fun sendNotificationToUser(username: String, message: Notification) {
///        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", message)
    }
}
