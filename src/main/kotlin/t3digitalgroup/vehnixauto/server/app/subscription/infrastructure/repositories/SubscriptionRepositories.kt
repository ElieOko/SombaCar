package t3digitalgroup.vehnixauto.server.app.subscription.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.subscription.infrastructure.entities.SubscriptionPlanEntity
import t3digitalgroup.vehnixauto.server.app.subscription.infrastructure.entities.UserSubscriptionEntity

interface SubscriptionPlanRepository : CoroutineCrudRepository<SubscriptionPlanEntity, Long> {
    @Query("SELECT * FROM subscription_plans WHERE is_active = TRUE ORDER BY price ASC")
    suspend fun findAllActive(): Flow<SubscriptionPlanEntity>
}

interface UserSubscriptionRepository : CoroutineCrudRepository<UserSubscriptionEntity, Long> {
    @Query(
        """
        SELECT * FROM user_subscriptions
        WHERE user_id = :userId AND status = 'ACTIVE' AND expires_at > NOW()
        ORDER BY expires_at DESC
        LIMIT 1
        """
    )
    suspend fun findActiveByUserId(userId: Long): UserSubscriptionEntity?

    @Query(
        """
        SELECT * FROM user_subscriptions
        WHERE user_id = :userId
        ORDER BY created_at DESC
        """
    )
    suspend fun findAllByUserId(userId: Long): Flow<UserSubscriptionEntity>
}
