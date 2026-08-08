package t3digitalgroup.vehnixauto.server.app.glossary.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.glossary.infrastructure.entities.GlossaryEntryEntity

interface GlossaryEntryRepository : CoroutineCrudRepository<GlossaryEntryEntity, Long> {
    @Query("SELECT * FROM glossary_entries WHERE status = 'ACTIVE' ORDER BY official_name ASC")
    suspend fun findAllActive(): Flow<GlossaryEntryEntity>

    @Query("SELECT * FROM glossary_entries ORDER BY official_name ASC")
    suspend fun findAllOrdered(): Flow<GlossaryEntryEntity>

    @Query("SELECT * FROM glossary_entries WHERE status = 'ACTIVE' AND category = :category ORDER BY official_name ASC")
    suspend fun findActiveByCategory(category: String): Flow<GlossaryEntryEntity>

    @Query(
        """
        SELECT * FROM glossary_entries
        WHERE status = 'ACTIVE'
          AND (
            LOWER(official_name) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(category) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(local_names) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(description) LIKE LOWER(CONCAT('%', :query, '%'))
          )
        ORDER BY official_name ASC
        """
    )
    suspend fun searchActive(query: String): Flow<GlossaryEntryEntity>
}
