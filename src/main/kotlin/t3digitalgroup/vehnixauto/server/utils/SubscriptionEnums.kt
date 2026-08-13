package t3digitalgroup.vehnixauto.server.utils

enum class PaymentPurpose {
    PURCHASE,
    SUBSCRIPTION,
}

enum class SubscriptionStatus {
    ACTIVE,
    EXPIRED,
    CANCELLED,
}

enum class MechanicContactStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    COMPLETED,
}
