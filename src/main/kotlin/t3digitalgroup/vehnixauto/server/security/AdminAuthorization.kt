package t3digitalgroup.vehnixauto.server.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class AdminAuthorization(
    private val auth: Auth,
    @Value("\${app.api-test-mode:false}") private val apiTestMode: Boolean,
) {
    suspend fun requireAdmin(): Long {
        if (apiTestMode) return ApiTestSecuritySupport.TEST_USER_ID
        val session = auth.user()
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise")
        val isAdmin = session.second.any { it }
        if (!isAdmin) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Accès non autorisé")
        }
        return session.first?.userId
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur introuvable")
    }

    suspend fun isAdmin(): Boolean {
        if (apiTestMode) return true
        val session = auth.user() ?: return false
        return session.second.any { it }
    }
}
