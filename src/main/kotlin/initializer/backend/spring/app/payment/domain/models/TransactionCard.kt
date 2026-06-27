package initializer.backend.spring.app.payment.domain.models

import jakarta.validation.constraints.NotNull

data class TransactionCard(
    var authorization: String = "",
    val merchant: String = "carNayo",
    val reference: String,
    val amount: String,
    val currency: String = "USD",
    val description: String = "Paiement sur la plateforme CarNayo",
    val callback_url: String = "https://api.carnayo.com/api/v1/public/payment/card/callback",
    val approve_url: String = "https://carnayo.com/services",
    val cancel_url: String = "https://carnayo.com/listings",
    val decline_url: String = "https://carnayo.com/contact"
)

data class TransactionCardRequest(
    @NotNull
    val deviseId: Long,
)
