package t3digitalgroup.vehnixauto.server.app.sale.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.sale.infrastructure.entities.SaleOfferEntity

interface SaleOfferRepository : CoroutineCrudRepository<SaleOfferEntity, Long> {
    @Query("SELECT * FROM sale_offers WHERE status = 'ACTIVE' ORDER BY created_at DESC")
    suspend fun findAllActive(): Flow<SaleOfferEntity>

    @Query("SELECT * FROM sale_offers ORDER BY created_at DESC")
    suspend fun findAllOrdered(): Flow<SaleOfferEntity>

    @Query("SELECT * FROM sale_offers WHERE status = 'ACTIVE' AND offer_type = :offerType ORDER BY created_at DESC")
    suspend fun findActiveByType(offerType: String): Flow<SaleOfferEntity>
}
