package t3digitalgroup.vehnixauto.server.app.moto.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.moto.infrastructure.entities.MotoModelEntity

interface MotoModelRepository : CoroutineCrudRepository<MotoModelEntity, Long> {
    @Query("SELECT * FROM moto_models WHERE brand = :brand AND model = :model LIMIT 1")
    suspend fun findByBrandAndModel(brand: String, model: String): MotoModelEntity?

    @Query("SELECT * FROM moto_models WHERE brand = :brand ORDER BY model ASC")
    suspend fun findByBrand(brand: String): Flow<MotoModelEntity>
}
