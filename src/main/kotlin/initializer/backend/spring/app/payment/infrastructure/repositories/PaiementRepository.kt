package initializer.backend.spring.app.payment.infrastructure.repositories

import initializer.backend.spring.app.payment.infrastructure.entities.PaiementEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PaiementRepository : CoroutineCrudRepository<PaiementEntity, Long> {
    @Query("SELECT * FROM paiements WHERE reference = :codeReference")
    suspend fun findByReference(codeReference: String): Flow<PaiementEntity?>

    @Query("SELECT * FROM paiements WHERE user_id = :userId")
    suspend fun findByUser(userId: Long): Flow<PaiementEntity?>
}
