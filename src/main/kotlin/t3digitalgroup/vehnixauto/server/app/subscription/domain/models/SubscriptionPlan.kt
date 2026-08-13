package t3digitalgroup.vehnixauto.server.app.subscription.domain.models

import java.time.LocalDateTime

data class SubscriptionPlan(
    val planId: Long? = null,
    val name: String,
    val description: String? = null,
    val price: String,
    val devise: String = "USD",
    val durationDays: Int,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)

data class UserSubscription(
    val subscriptionId: Long? = null,
    val userId: Long,
    val planId: Long,
    val paymentReference: String? = null,
    val status: String,
    val startsAt: LocalDateTime,
    val expiresAt: LocalDateTime,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)

data class SubscriptionStatusResponse(
    val isPremium: Boolean,
    val premiumExpiresAt: LocalDateTime?,
    val activeSubscription: UserSubscription?,
)
