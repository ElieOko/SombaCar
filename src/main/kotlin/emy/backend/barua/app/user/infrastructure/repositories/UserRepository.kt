package emy.backend.barua.app.user.infrastructure.repositories

import emy.backend.barua.app.user.infrastructure.entities.UserEntity
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserRepository : CoroutineCrudRepository<UserEntity, Long> {

    @Query("SELECT * FROM users WHERE email = :identifier OR phone = :identifier AND is_lock = false")
   suspend fun findByPhoneOrEmail(identifier: String) : UserEntity?

    @Modifying
    @Query("""UPDATE users
    SET is_lock = :lock 
    WHERE id = :userId"""
    )
    suspend fun isLock(userId: Long, lock: Boolean = true): Int
}