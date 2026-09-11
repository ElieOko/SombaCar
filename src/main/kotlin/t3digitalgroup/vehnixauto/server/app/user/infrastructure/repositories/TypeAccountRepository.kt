package t3digitalgroup.vehnixauto.server.app.user.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.entities.TypeAccountEntity

interface TypeAccountRepository : CoroutineCrudRepository<TypeAccountEntity, Long> {
    @Query("SELECT * FROM type_accounts ORDER BY id ASC")
    suspend fun findAllOrdered(): Flow<TypeAccountEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM type_accounts WHERE LOWER(name) = LOWER(:name))")
    suspend fun existsByNameIgnoreCase(name: String): Boolean
}
