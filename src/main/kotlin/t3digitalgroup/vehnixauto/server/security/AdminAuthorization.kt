package t3digitalgroup.vehnixauto.server.security

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class AdminAuthorization(
    private val auth: Auth,
) {
    suspend fun requireAdmin(): Long {
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
        val session = auth.user() ?: return false
        return session.second.any { it }
    }
}
