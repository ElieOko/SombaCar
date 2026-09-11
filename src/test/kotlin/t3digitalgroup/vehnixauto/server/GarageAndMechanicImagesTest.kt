package t3digitalgroup.vehnixauto.server

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import t3digitalgroup.vehnixauto.server.app.garage.application.services.GarageImageService
import t3digitalgroup.vehnixauto.server.app.garage.application.services.GarageService
import t3digitalgroup.vehnixauto.server.app.garage.domain.models.GarageImage
import t3digitalgroup.vehnixauto.server.app.garage.infrastructure.entities.GarageEntity
import t3digitalgroup.vehnixauto.server.app.garage.infrastructure.repositories.GarageRepository
import t3digitalgroup.vehnixauto.server.app.mechanic.application.services.MechanicImageService
import t3digitalgroup.vehnixauto.server.app.mechanic.application.services.MechanicService
import t3digitalgroup.vehnixauto.server.app.mechanic.domain.models.MechanicImage
import t3digitalgroup.vehnixauto.server.app.mechanic.infrastructure.entities.MechanicEntity
import t3digitalgroup.vehnixauto.server.app.mechanic.infrastructure.repositories.MechanicContactRepository
import t3digitalgroup.vehnixauto.server.app.mechanic.infrastructure.repositories.MechanicRepository
import t3digitalgroup.vehnixauto.server.app.notification.infrastructure.repositories.NotificationRepository

class GarageAndMechanicImagesTest {

    @Test
    fun `garage responses include images`() = runTest {
        val repository = mock<GarageRepository>()
        val garageImageService = mock<GarageImageService>()
        val entity = GarageEntity(
            garageId = 1L,
            userId = 2L,
            name = "Garage Central",
            garageType = "CAR",
        )
        whenever(repository.findById(1L)).thenReturn(entity)
        whenever(garageImageService.findByGarageIdIn(listOf(1L))).thenReturn(
            listOf(
                GarageImage(garageImageId = 10L, garageId = 1L, name = "front.jpg", path = "https://example/front.jpg"),
            ),
        )

        val service = GarageService(repository, garageImageService)
        val garage = service.findById(1L)

        assertEquals(1, garage.images.size)
        assertEquals("front.jpg", garage.images.first().name)
    }

    @Test
    fun `mechanic responses include images`() = runTest {
        val repository = mock<MechanicRepository>()
        val contactRepository = mock<MechanicContactRepository>()
        val notificationRepository = mock<NotificationRepository>()
        val mechanicImageService = mock<MechanicImageService>()
        val entity = MechanicEntity(
            mechanicId = 3L,
            fullName = "Jean Mecano",
            phone = "+243000",
        )
        whenever(repository.findNightAvailable()).thenReturn(flowOf(entity))
        whenever(mechanicImageService.findByMechanicIdIn(listOf(3L))).thenReturn(
            listOf(
                MechanicImage(mechanicImageId = 20L, mechanicId = 3L, name = "profile.jpg", path = "https://example/profile.jpg"),
            ),
        )

        val service = MechanicService(
            repository,
            contactRepository,
            notificationRepository,
            mechanicImageService,
        )
        val mechanics = service.findNightAvailable()

        assertEquals(1, mechanics.size)
        assertEquals(1, mechanics.first().images.size)
        assertEquals("profile.jpg", mechanics.first().images.first().name)
    }
}
