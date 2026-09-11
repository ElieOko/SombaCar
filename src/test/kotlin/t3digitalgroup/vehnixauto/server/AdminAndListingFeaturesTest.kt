package t3digitalgroup.vehnixauto.server

import jakarta.servlet.http.HttpServletRequest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.admin.application.services.AdminService
import t3digitalgroup.vehnixauto.server.app.admin.infrastructure.controllers.AdminController
import t3digitalgroup.vehnixauto.server.app.car.application.services.CarListingService
import t3digitalgroup.vehnixauto.server.app.car.domain.models.CarListing
import t3digitalgroup.vehnixauto.server.app.car.infrastructure.repositories.CarListingRepository
import t3digitalgroup.vehnixauto.server.app.car.infrastructure.repositories.CarModelRepository
import t3digitalgroup.vehnixauto.server.app.moto.application.services.MotoListingService
import t3digitalgroup.vehnixauto.server.app.report.application.services.ListingReportService
import t3digitalgroup.vehnixauto.server.app.report.domain.models.request.ListingReportRequest
import t3digitalgroup.vehnixauto.server.app.report.infrastructure.entities.ListingReportEntity
import t3digitalgroup.vehnixauto.server.app.report.infrastructure.repositories.ListingReportRepository
import t3digitalgroup.vehnixauto.server.app.tools.application.services.PartListingService
import t3digitalgroup.vehnixauto.server.app.user.application.services.TypeAccountService
import t3digitalgroup.vehnixauto.server.app.user.domain.models.TypeAccount
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.controllers.TypeAccountController
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.entities.TypeAccountEntity
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.repositories.TypeAccountRepository
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.repositories.UserRepository
import t3digitalgroup.vehnixauto.server.security.AdminAuthorization
import t3digitalgroup.vehnixauto.server.security.monitoring.SentryService
import t3digitalgroup.vehnixauto.server.utils.ListingStatus
import t3digitalgroup.vehnixauto.server.utils.ListingType
import t3digitalgroup.vehnixauto.server.utils.OfferType
import t3digitalgroup.vehnixauto.server.utils.ensurePubliclyVisible

class AdminAndListingFeaturesTest {

    private val request = mock<HttpServletRequest>()
    private val sentry = mock<SentryService>()
    private val adminAuthorization = AdminAuthorization(mock(), apiTestMode = true)

    @Test
    fun `ensurePubliclyVisible rejects inactive and banned listings`() {
        assertThrows(ResponseStatusException::class.java) {
            ensurePubliclyVisible(ListingStatus.INACTIVE.name)
        }
        assertThrows(ResponseStatusException::class.java) {
            ensurePubliclyVisible(ListingStatus.BANNED.name)
        }
    }

    @Test
    fun `public car listing detail hides inactive listings`() = runTest {
        val listingRepository = mock<CarListingRepository>()
        val carModelRepository = mock<CarModelRepository>()
        val carImageService = mock<t3digitalgroup.vehnixauto.server.app.car.application.services.CarImageService>()
        val carDocumentService = mock<t3digitalgroup.vehnixauto.server.app.car.application.services.CarDocumentService>()
        val userRepository = mock<UserRepository>()

        val entity = t3digitalgroup.vehnixauto.server.app.car.infrastructure.entities.CarListingEntity(
            listingId = 1L,
            userId = 2L,
            carModelId = 3L,
            listingType = ListingType.SALE.name,
            year = 2020,
            status = ListingStatus.INACTIVE.name,
        )
        whenever(listingRepository.findById(1L)).thenReturn(entity)

        val service = CarListingService(
            listingRepository,
            carModelRepository,
            carImageService,
            carDocumentService,
            userRepository,
        )

        val error = try {
            service.findById(1L, requireActive = true)
            null
        } catch (e: ResponseStatusException) {
            e
        }
        assertNotNull(error)
        assertEquals(HttpStatus.NOT_FOUND, error!!.statusCode)
    }

    @Test
    fun `report service deactivates listing after threshold`() = runTest {
        val repository = mock<ListingReportRepository>()
        val carListingService = mock<CarListingService>()
        val partListingService = mock<PartListingService>()
        val motoListingService = mock<MotoListingService>()

        val activeListing = sampleCarListing(ListingStatus.ACTIVE)
        whenever(carListingService.findById(1L)).thenReturn(activeListing)
        whenever(repository.existsByReportedByAndListingTypeAndListingId(10L, OfferType.CAR.name, 1L)).thenReturn(false)
        whenever(repository.save(any())).thenAnswer {
            ListingReportEntity(
                reportId = 99L,
                listingType = OfferType.CAR.name,
                listingId = 1L,
                reportedBy = 10L,
                reason = "spam",
            )
        }
        whenever(repository.countByListing(OfferType.CAR.name, 1L)).thenReturn(3L)
        whenever(carListingService.updateStatus(1L, ListingStatus.INACTIVE)).thenReturn(
            sampleCarListing(ListingStatus.INACTIVE),
        )

        val service = ListingReportService(
            repository,
            carListingService,
            partListingService,
            motoListingService,
        )
        val result = service.report(
            10L,
            ListingReportRequest(
                listingType = OfferType.CAR,
                listingId = 1L,
                reason = "spam",
            ),
        )

        assertEquals(3, result.reportCount)
        assertEquals(true, result.listingDeactivated)
        verify(carListingService).updateStatus(1L, ListingStatus.INACTIVE)
    }

    @Test
    fun `report service skips deactivation when listing is no longer active`() = runTest {
        val repository = mock<ListingReportRepository>()
        val carListingService = mock<CarListingService>()
        val partListingService = mock<PartListingService>()
        val motoListingService = mock<MotoListingService>()

        val activeListing = sampleCarListing(ListingStatus.ACTIVE)
        val inactiveListing = sampleCarListing(ListingStatus.INACTIVE)
        whenever(carListingService.findById(1L)).thenReturn(activeListing, inactiveListing)
        whenever(repository.existsByReportedByAndListingTypeAndListingId(10L, OfferType.CAR.name, 1L)).thenReturn(false)
        whenever(repository.save(any())).thenAnswer {
            ListingReportEntity(
                reportId = 99L,
                listingType = OfferType.CAR.name,
                listingId = 1L,
                reportedBy = 10L,
                reason = "spam",
            )
        }
        whenever(repository.countByListing(OfferType.CAR.name, 1L)).thenReturn(3L)

        val service = ListingReportService(
            repository,
            carListingService,
            partListingService,
            motoListingService,
        )
        val result = service.report(
            10L,
            ListingReportRequest(
                listingType = OfferType.CAR,
                listingId = 1L,
                reason = "spam",
            ),
        )

        assertEquals(3, result.reportCount)
        assertFalse(result.listingDeactivated)
    }

    @Test
    fun `type account service returns all persisted types`() = runTest {
        val repository = mock<TypeAccountRepository>()
        whenever(repository.findAllOrdered()).thenReturn(
            flowOf(
                TypeAccountEntity(id = 1L, name = "client"),
                TypeAccountEntity(id = 2L, name = "manager"),
                TypeAccountEntity(id = 3L, name = "recruiter"),
            ),
        )

        val service = TypeAccountService(repository)
        val types = service.getAll()

        assertEquals(3, types.size)
        assertEquals("recruiter", types.last().name)
    }

    @Test
    fun `public type account endpoint returns all types`() = runTest {
        val typeAccountService = mock<TypeAccountService>()
        whenever(typeAccountService.getAll()).thenReturn(
            listOf(
                TypeAccount(1L, "client"),
                TypeAccount(2L, "manager"),
                TypeAccount(3L, "recruiter"),
            ),
        )

        val controller = TypeAccountController(typeAccountService, adminAuthorization, sentry)
        val response = controller.getAllTypeAccountE(request, "v1")

        assertEquals(3, response.data.size)
    }

    @Test
    fun `admin unban endpoint reactivates listing`() = runTest {
        val adminService = mock<AdminService>()
        val reactivated = sampleCarListing(ListingStatus.ACTIVE)
        whenever(adminService.unbanCarListing(7L)).thenReturn(reactivated)

        val controller = AdminController(adminService, adminAuthorization, sentry)
        val response = controller.unbanCarListing(request, "v1", 7L)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
    }

    private fun sampleCarListing(status: ListingStatus) = CarListing(
        listingId = 1L,
        userId = 2L,
        carModelId = 3L,
        listingType = ListingType.SALE.name,
        year = 2020,
        isElectric = false,
        mileageKm = 1000,
        condition = "USED",
        price = "10000",
        status = status.name,
    )
}
