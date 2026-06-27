package initializer.backend.spring.app.payment.domain.models

data class FlexCheck(
    val code: String,
    val message: String,
    val transaction: TransactionState?
)
