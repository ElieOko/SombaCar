package t3digitalgroup.vehnixauto.server.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.repositories.UserRepository
import java.time.LocalDateTime

@Component
class PremiumAuthorization(
    private val auth: Auth,
    private val userRepository: UserRepository,
    @Value("\${app.api-test-mode:false}") private val apiTestMode: Boolean,
) {
    suspend fun requirePremium(): Long {
        if (apiTestMode) return ApiTestSecuritySupport.TEST_USER_ID
        val userId = auth.user()?.first?.userId
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise")
        if (!isPremium(userId)) {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Abonnement premium requis pour accéder à cette fonctionnalité.",
            )
        }
        return userId
    }

    suspend fun isPremium(userId: Long): Boolean {
        if (apiTestMode) return true
        val user = userRepository.findById(userId) ?: return false
        if (!user.isPremium) return false
        val expiresAt = user.premiumExpiresAt ?: return true
        return expiresAt.isAfter(LocalDateTime.now())
    }
}
