package t3digitalgroup.vehnixauto.server.security

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.user.domain.models.UserDto
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.entities.UserEntity
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.repositories.UserRepository
import java.time.LocalDateTime

class ApiSecurityRestoreTest {

    @Test
    fun `temporary public patterns cover new modules only`() {
        assertTrue(ApiTestSecuritySupport.temporaryPublicPatterns.any { it.contains("cart") })
        assertTrue(ApiTestSecuritySupport.temporaryPublicPatterns.any { it.contains("favorites") })
        assertTrue(ApiTestSecuritySupport.temporaryPublicPatterns.any { it.contains("garages") })
        assertFalse(ApiTestSecuritySupport.temporaryPublicPatterns.any { it.contains("/public/") })
    }

    @Test
    fun `premium bypass disabled when test mode off`() = runTest {
        val auth = mock<Auth>()
        val userRepository = mock<UserRepository>()
        whenever(auth.user()).thenReturn(
            Pair(
                UserDto(
                    userId = 1L,
                    email = "u@test.com",
                    username = "@u",
                    phone = "1",
                    city = "Kin",
                    firstName = "A",
                    lastName = "B",
                    isPremium = false,
                    isCertified = false,
                ),
                mutableListOf(false),
            ),
        )
        whenever(userRepository.findById(1L)).thenReturn(
            UserEntity(
                userId = 1L,
                firstName = "A",
                lastName = "B",
                city = "Kin",
                isPremium = false,
            ),
        )

        val premiumAuthorization = PremiumAuthorization(auth, userRepository, apiTestMode = false)
        val error = try {
            premiumAuthorization.requirePremium()
            null
        } catch (e: ResponseStatusException) {
            e
        }
        assertNotNull(error)
        assertEquals(HttpStatus.FORBIDDEN, error!!.statusCode)
    }

    @Test
    fun `premium bypass enabled only in explicit test mode`() = runTest {
        val auth = mock<Auth>()
        val userRepository = mock<UserRepository>()
        val premiumAuthorization = PremiumAuthorization(auth, userRepository, apiTestMode = true)

        assertEquals(ApiTestSecuritySupport.TEST_USER_ID, premiumAuthorization.requirePremium())
        assertTrue(premiumAuthorization.isPremium(999L))
    }
}
