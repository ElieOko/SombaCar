package t3digitalgroup.vehnixauto.server.app.payment.domain.models

enum class StatusPayment {
    PENDING,
    SUCCESS,
    CANCELLED
}

enum class TypePayment {
    MOBILE_MONEY,
    CARD
}

enum class DeviseType {
    USD,
    CDF
}
