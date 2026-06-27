package initializer.backend.spring.app.payment.domain.models

import jakarta.validation.constraints.NotNull

data class TransactionState(
    val orderNumber: String,
    val reference: String,
    val amount: String,
    val amountCustomer: String,
    val currency: String,
    val createdAt: String,
    val status: String
)

data class Transaction(
    val merchant: String = "carNayo",
    val type: String = "1",
    val reference: String,
    val phone: String,
    val amount: String = "5",
    val currency: String = "USD",
    val callbackUrl: String = "https://api.carnayo.com/api/v1/public/payment/mobile/callback"
)

data class TransactionRequest(
    @NotNull
    val phone: String,
    @NotNull
    val deviseId: Long,
)
