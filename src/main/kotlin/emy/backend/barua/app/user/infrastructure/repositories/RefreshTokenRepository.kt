package emy.backend.barua.app.user.infrastructure.repositories

import emy.backend.barua.app.user.infrastructure.entities.RefreshToken
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface RefreshTokenRepository : CoroutineCrudRepository<RefreshToken, Long> {
    suspend fun findByUserIdAndHashedToken(userId: Long, hashedToken: String): RefreshToken?
    suspend fun deleteByUserIdAndHashedToken(userId: Long, hashedToken: String)
}