package t3digitalgroup.vehnixauto.server.app.payment.infrastructure.repositories

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.payment.infrastructure.entities.DeviseEntity

interface DeviseRepository : CoroutineCrudRepository<DeviseEntity, Long>
