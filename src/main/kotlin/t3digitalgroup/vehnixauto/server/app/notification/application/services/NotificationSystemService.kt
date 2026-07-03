package t3digitalgroup.vehnixauto.server.app.notification.application.services

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.notification.domain.models.NotificationSystem
import t3digitalgroup.vehnixauto.server.app.notification.infrastructure.mapper.*
import t3digitalgroup.vehnixauto.server.app.notification.infrastructure.repositories.*
import t3digitalgroup.vehnixauto.server.utils.Mode

@Service
@Profile(Mode.DEV)
class NotificationSystemService(
    private val repository: NotificationSystemRepository,
    private val notificationRepository: NotificationRepository,
) {
    suspend fun create(notice: NotificationSystem): NotificationSystem {
        val data = repository.save(notice.toEntity())
        return data.toDomain()
    }

    suspend fun notificationByUser(id: Long) = coroutineScope {
        notificationRepository.getAllByUser(id).map { it?.toDomain() }.toList()
    }

    suspend fun notificationDisable(id: Long) = coroutineScope {
        val data = notificationRepository.findById(id)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette notification n'existe pas")
        data.isActive = false
        notificationRepository.save(data)
    }
}
