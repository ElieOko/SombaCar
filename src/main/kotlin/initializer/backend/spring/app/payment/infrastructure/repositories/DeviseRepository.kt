package initializer.backend.spring.app.payment.infrastructure.repositories

import initializer.backend.spring.app.payment.infrastructure.entities.DeviseEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface DeviseRepository : CoroutineCrudRepository<DeviseEntity, Long>
