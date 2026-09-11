package t3digitalgroup.vehnixauto.server

import jakarta.servlet.http.HttpServletRequest
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.cart.application.services.CartService
import t3digitalgroup.vehnixauto.server.app.cart.infrastructure.controllers.CartController
import t3digitalgroup.vehnixauto.server.app.favorite.application.services.ListingFavoriteService
import t3digitalgroup.vehnixauto.server.app.favorite.infrastructure.controllers.ListingFavoriteController
import t3digitalgroup.vehnixauto.server.app.garage.application.services.GarageService
import t3digitalgroup.vehnixauto.server.app.garage.infrastructure.controllers.GarageController
import t3digitalgroup.vehnixauto.server.app.mechanic.application.services.MechanicService
import t3digitalgroup.vehnixauto.server.app.mechanic.infrastructure.controllers.MechanicController
import t3digitalgroup.vehnixauto.server.app.report.application.services.ListingReportService
import t3digitalgroup.vehnixauto.server.app.report.infrastructure.controllers.ListingReportController
import t3digitalgroup.vehnixauto.server.app.subscription.application.services.SubscriptionPlanService
import t3digitalgroup.vehnixauto.server.app.subscription.application.services.UserSubscriptionService
import t3digitalgroup.vehnixauto.server.app.subscription.domain.models.SubscriptionStatusResponse
import t3digitalgroup.vehnixauto.server.app.subscription.infrastructure.controllers.SubscriptionController
import t3digitalgroup.vehnixauto.server.app.user.application.services.AuthService
import t3digitalgroup.vehnixauto.server.app.user.domain.models.UserDto
import t3digitalgroup.vehnixauto.server.app.user.domain.models.request.ForgotPasswordRequest
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.controllers.AuthController
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.repositories.UserRepository
import t3digitalgroup.vehnixauto.server.security.AdminAuthorization
import t3digitalgroup.vehnixauto.server.security.Auth
import t3digitalgroup.vehnixauto.server.security.PremiumAuthorization
import t3digitalgroup.vehnixauto.server.security.monitoring.SentryService
import t3digitalgroup.vehnixauto.server.utils.OfferType
import t3digitalgroup.vehnixauto.server.utils.mail.SenderMailAuth

class NewFeaturesApiTest {

    private val request = mock<HttpServletRequest>()
    private val sentry = mock<SentryService>()
    private val auth = mock<Auth>()

    private val testUser = UserDto(
        userId = 1L,
        email = "test@example.com",
        username = "@test",
        phone = "+243000",
        city = "Kinshasa",
        firstName = "Test",
        lastName = "User",
        isPremium = false,
        isCertified = false,
    )

    private val premiumAuthorization = PremiumAuthorization(auth, mock(), apiTestMode = true)
    private val adminAuthorization = AdminAuthorization(auth, apiTestMode = true)

    @Test
    fun `cart active endpoint responds`() = runTest {
        val cartService = mock<CartService>()
        whenever(auth.user()).thenReturn(Pair(testUser, mutableListOf(false)))
        whenever(cartService.findActiveByUserId(1L)).thenReturn(emptyList())

        val controller = CartController(cartService, auth, sentry)
        val response = controller.findActive(request, "v1")

        assertNotNull(response)
    }

    @Test
    fun `favorites list endpoint responds`() = runTest {
        val favoriteService = mock<ListingFavoriteService>()
        whenever(auth.user()).thenReturn(Pair(testUser, mutableListOf(false)))
        whenever(favoriteService.findByUserId(1L)).thenReturn(emptyList())

        val controller = ListingFavoriteController(favoriteService, auth, sentry)
        val response = controller.findAll(request, "v1")

        assertNotNull(response)
    }

    @Test
    fun `listing report count endpoint responds`() = runTest {
        val reportService = mock<ListingReportService>()
        whenever(reportService.countByListing(OfferType.CAR, 1L)).thenReturn(0)

        val controller = ListingReportController(reportService, auth, adminAuthorization, sentry)
        val response = controller.countByListing(request, "v1", OfferType.CAR, 1L)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(0, response.body?.get("reportCount"))
        assertEquals(3, response.body?.get("deactivationThreshold"))
    }

    @Test
    fun `garage browse endpoint responds with premium test bypass`() = runTest {
        val garageService = mock<GarageService>()
        whenever(garageService.findAllActive()).thenReturn(emptyList())

        val controller = GarageController(
            garageService,
            mock(),
            auth,
            sentry,
            mock(),
            mock(),
        )
        val response = controller.findAllActive(request, "v1")

        assertNotNull(response)
    }

    @Test
    fun `subscription plans endpoint responds`() = runTest {
        val planService = mock<SubscriptionPlanService>()
        whenever(planService.findAllActive()).thenReturn(emptyList())

        val controller = SubscriptionController(
            planService,
            mock(),
            mock(),
            auth,
            adminAuthorization,
            sentry,
        )
        val response = controller.findPlans(request, "v1")

        assertNotNull(response)
    }

    @Test
    fun `subscription status endpoint responds`() = runTest {
        val userSubscriptionService = mock<UserSubscriptionService>()
        whenever(auth.user()).thenReturn(Pair(testUser, mutableListOf(false)))
        whenever(userSubscriptionService.getStatus(1L)).thenReturn(
            SubscriptionStatusResponse(isPremium = false, premiumExpiresAt = null, activeSubscription = null),
        )

        val controller = SubscriptionController(
            mock(),
            userSubscriptionService,
            mock(),
            auth,
            adminAuthorization,
            sentry,
        )
        val response = controller.status(request, "v1")

        assertNotNull(response)
    }

    @Test
    fun `mechanics night available endpoint responds with premium test bypass`() = runTest {
        val mechanicService = mock<MechanicService>()
        whenever(mechanicService.findNightAvailable()).thenReturn(emptyList())

        val controller = MechanicController(
            mechanicService,
            mock(),
            auth,
            premiumAuthorization,
            adminAuthorization,
            sentry,
            mock(),
            mock(),
        )
        val response = controller.findNightAvailable(request, "v1")

        assertNotNull(response)
    }

    @Test
    fun `forgot password public endpoint is reachable`() = runTest {
        val authService = mock<AuthService>()
        whenever(authService.forgotPassword(any(), any(), any())).thenThrow(
            ResponseStatusException(HttpStatus.BAD_REQUEST, "Code invalide."),
        )

        val controller = AuthController(
            authService,
            auth,
            sentry,
            mock<SenderMailAuth>(),
            mock<UserRepository>(),
        )

        val error = try {
            controller.forgotPassword(
                request,
                ForgotPasswordRequest(
                    identifier = "test@example.com",
                    code = "000000",
                    newPassword = "secret12",
                ),
                "v1",
            )
            null
        } catch (e: ResponseStatusException) {
            e
        }

        assertNotNull(error)
        assertEquals(HttpStatus.BAD_REQUEST, error!!.statusCode)
    }

    @Test
    fun `temporary public patterns cover all new protected modules`() {
        val patterns = t3digitalgroup.vehnixauto.server.security.ApiTestSecuritySupport.temporaryPublicPatterns
        assertTrue(patterns.any { it.contains("cart") })
        assertTrue(patterns.any { it.contains("favorites") })
        assertTrue(patterns.any { it.contains("listing-reports") })
        assertTrue(patterns.any { it.contains("garages") })
        assertTrue(patterns.any { it.contains("subscriptions") })
        assertTrue(patterns.any { it.contains("mechanics") })
    }
}
