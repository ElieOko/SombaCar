package t3digitalgroup.vehnixauto.server.app.subscription.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "subscription_plans")
class SubscriptionPlanEntity(
    @Id
    @Column("id")
    val planId: Long? = null,
    @Column("name")
    var name: String,
    @Column("description")
    var description: String? = null,
    @Column("price")
    var price: String,
    @Column("devise")
    var devise: String = "USD",
    @Column("duration_days")
    var durationDays: Int,
    @Column("is_active")
    var isActive: Boolean = true,
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column("updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)

@Table(name = "user_subscriptions")
class UserSubscriptionEntity(
    @Id
    @Column("id")
    val subscriptionId: Long? = null,
    @Column("user_id")
    val userId: Long,
    @Column("plan_id")
    val planId: Long,
    @Column("payment_reference")
    val paymentReference: String? = null,
    @Column("status")
    var status: String,
    @Column("starts_at")
    val startsAt: LocalDateTime,
    @Column("expires_at")
    val expiresAt: LocalDateTime,
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
