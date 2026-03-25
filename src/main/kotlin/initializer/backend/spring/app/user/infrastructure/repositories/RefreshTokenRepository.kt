package initializer.backend.spring.app.user.infrastructure.repositories

import initializer.backend.spring.app.user.infrastructure.entities.RefreshToken
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface RefreshTokenRepository : CoroutineCrudRepository<RefreshToken, Long> {
    suspend fun findByUserIdAndHashedToken(userId: Long, hashedToken: String): RefreshToken?
    suspend fun deleteByUserIdAndHashedToken(userId: Long, hashedToken: String)
}