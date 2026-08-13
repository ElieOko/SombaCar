package t3digitalgroup.vehnixauto.server.app.subscription.infrastructure.mapper

import t3digitalgroup.vehnixauto.server.app.subscription.domain.models.SubscriptionPlan
import t3digitalgroup.vehnixauto.server.app.subscription.domain.models.UserSubscription
import t3digitalgroup.vehnixauto.server.app.subscription.infrastructure.entities.SubscriptionPlanEntity
import t3digitalgroup.vehnixauto.server.app.subscription.infrastructure.entities.UserSubscriptionEntity

fun SubscriptionPlanEntity.toDomain() = SubscriptionPlan(
    planId = this.planId,
    name = this.name,
    description = this.description,
    price = this.price,
    devise = this.devise,
    durationDays = this.durationDays,
    isActive = this.isActive,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

fun SubscriptionPlan.toEntity() = SubscriptionPlanEntity(
    planId = this.planId,
    name = this.name,
    description = this.description,
    price = this.price,
    devise = this.devise,
    durationDays = this.durationDays,
    isActive = this.isActive,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

fun UserSubscriptionEntity.toDomain() = UserSubscription(
    subscriptionId = this.subscriptionId,
    userId = this.userId,
    planId = this.planId,
    paymentReference = this.paymentReference,
    status = this.status,
    startsAt = this.startsAt,
    expiresAt = this.expiresAt,
    createdAt = this.createdAt,
)

fun UserSubscription.toEntity() = UserSubscriptionEntity(
    subscriptionId = this.subscriptionId,
    userId = this.userId,
    planId = this.planId,
    paymentReference = this.paymentReference,
    status = this.status,
    startsAt = this.startsAt,
    expiresAt = this.expiresAt,
    createdAt = this.createdAt,
)
