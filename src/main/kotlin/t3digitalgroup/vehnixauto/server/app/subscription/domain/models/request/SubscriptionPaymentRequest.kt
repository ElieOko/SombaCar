package t3digitalgroup.vehnixauto.server.app.subscription.domain.models.request

import jakarta.validation.constraints.NotNull

data class SubscriptionPaymentRequest(
    @NotNull
    val planId: Long,
    @NotNull
    val deviseId: Long,
    @NotNull
    val phone: String,
)

data class SubscriptionCardPaymentRequest(
    @NotNull
    val planId: Long,
    @NotNull
    val deviseId: Long,
)

data class SubscriptionPlanRequest(
    @NotNull
    val name: String,
    val description: String? = null,
    @NotNull
    val price: String,
    val devise: String = "USD",
    @NotNull
    val durationDays: Int,
)
