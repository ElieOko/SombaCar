package t3digitalgroup.vehnixauto.server.app.car.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.car.infrastructure.entities.CarModelEntity

interface CarModelRepository : CoroutineCrudRepository<CarModelEntity, Long> {
    @Query("SELECT * FROM car_models WHERE brand = :brand AND model = :model LIMIT 1")
    suspend fun findByBrandAndModel(brand: String, model: String): CarModelEntity?

    @Query("SELECT * FROM car_models WHERE brand = :brand ORDER BY model ASC")
    suspend fun findByBrand(brand: String): Flow<CarModelEntity>
}
