package t3digitalgroup.vehnixauto.server.app.mechanic.application.services

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.mechanic.domain.models.Mechanic
import t3digitalgroup.vehnixauto.server.app.mechanic.domain.models.MechanicContactRequest
import t3digitalgroup.vehnixauto.server.app.mechanic.domain.models.request.MechanicContactRequestBody
import t3digitalgroup.vehnixauto.server.app.mechanic.domain.models.request.MechanicRequest
import t3digitalgroup.vehnixauto.server.app.mechanic.infrastructure.mapper.toDomain
import t3digitalgroup.vehnixauto.server.app.mechanic.infrastructure.mapper.toEntity
import t3digitalgroup.vehnixauto.server.app.mechanic.infrastructure.repositories.MechanicContactRepository
import t3digitalgroup.vehnixauto.server.app.mechanic.infrastructure.repositories.MechanicRepository
import t3digitalgroup.vehnixauto.server.app.notification.domain.models.TagType
import t3digitalgroup.vehnixauto.server.app.notification.infrastructure.entities.NotificationEntity
import t3digitalgroup.vehnixauto.server.app.notification.infrastructure.repositories.NotificationRepository
import t3digitalgroup.vehnixauto.server.utils.MechanicContactStatus
import t3digitalgroup.vehnixauto.server.utils.Mode
import t3digitalgroup.vehnixauto.server.utils.validateGeoCoordinates
import java.time.LocalDateTime

@Service
@Profile(Mode.DEV)
class MechanicService(
    private val repository: MechanicRepository,
    private val contactRepository: MechanicContactRepository,
    private val notificationRepository: NotificationRepository,
    private val mechanicImageService: MechanicImageService,
) {
    suspend fun create(request: MechanicRequest): Mechanic {
        validateGeoCoordinates(request.latitude, request.longitude)
        val saved = repository.save(
            Mechanic(
                garageId = request.garageId,
                fullName = request.fullName,
                phone = request.phone,
                city = request.city,
                latitude = request.latitude,
                longitude = request.longitude,
                isNightAvailable = request.isNightAvailable,
            ).toEntity(),
        ).toDomain()
        return enrichMechanics(listOf(saved)).first()
    }

    suspend fun addImages(mechanicId: Long, files: List<MultipartFile>): Mechanic {
        findById(mechanicId)
        files.filter { !it.isEmpty }.forEach { file ->
            mechanicImageService.createFromFile(mechanicId, file)
        }
        return findById(mechanicId)
    }

    suspend fun findById(id: Long): Mechanic =
        repository.findById(id)?.toDomain()?.let { enrichMechanics(listOf(it)).first() }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Mécanicien introuvable.")

    suspend fun findNightAvailable(): List<Mechanic> =
        enrichMechanics(repository.findNightAvailable().map { it.toDomain() }.toList())

    suspend fun findAllActive(): List<Mechanic> =
        enrichMechanics(repository.findAllActive().map { it.toDomain() }.toList())

    suspend fun contact(userId: Long, request: MechanicContactRequestBody): MechanicContactRequest {
        val mechanic = findById(request.mechanicId)
        if (!mechanic.isActive) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce mécanicien n'est plus disponible.")
        }
        if (!mechanic.isNightAvailable) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce mécanicien n'est pas disponible pour le dépannage nocturne.")
        }

        val contact = contactRepository.save(
            MechanicContactRequest(
                userId = userId,
                mechanicId = request.mechanicId,
                message = request.message,
                status = MechanicContactStatus.PENDING.name,
            ).toEntity(),
        ).toDomain()

        notificationRepository.save(
            NotificationEntity(
                userId = userId,
                title = "Demande de dépannage envoyée",
                message = "Votre demande a été transmise à ${mechanic.fullName}.",
                tag = TagType.SECURITY.name,
            ),
        )

        return contact
    }

    suspend fun findContactsByUser(userId: Long): List<MechanicContactRequest> =
        contactRepository.findByUserId(userId).map { it.toDomain() }.toList()

    suspend fun deactivate(mechanicId: Long): Mechanic {
        val entity = repository.findById(mechanicId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Mécanicien introuvable.")
        entity.isActive = false
        entity.updatedAt = LocalDateTime.now()
        return enrichMechanics(listOf(repository.save(entity).toDomain())).first()
    }

    private suspend fun enrichMechanics(mechanics: List<Mechanic>): List<Mechanic> {
        if (mechanics.isEmpty()) return mechanics
        val ids = mechanics.mapNotNull { it.mechanicId }
        if (ids.isEmpty()) return mechanics

        val imagesByMechanicId = mechanicImageService.findByMechanicIdIn(ids)
            .groupBy { it.mechanicId }

        return mechanics.map { mechanic ->
            mechanic.copy(images = imagesByMechanicId[mechanic.mechanicId].orEmpty())
        }
    }
}
