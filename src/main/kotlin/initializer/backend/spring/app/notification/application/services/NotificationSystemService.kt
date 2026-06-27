package initializer.backend.spring.app.notification.application.services

import initializer.backend.spring.app.notification.domain.models.NotificationSystem
import initializer.backend.spring.app.notification.infrastructure.mapper.toDomain
import initializer.backend.spring.app.notification.infrastructure.mapper.toEntity
import initializer.backend.spring.app.notification.infrastructure.repositories.NotificationRepository
import initializer.backend.spring.app.notification.infrastructure.repositories.NotificationSystemRepository
import initializer.backend.spring.utils.Mode
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

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
