package t3digitalgroup.vehnixauto.server.app.payment.domain.models

import jakarta.validation.constraints.NotNull

data class TransactionCard(
    var authorization: String = "",
    val merchant: String = "vehnixauto",
    val reference: String,
    val amount: String,
    val currency: String = "USD",
    val description: String = "Paiement sur la plateforme VehnixAuto",
    val callback_url: String = "https://api.vehnixauto.com/api/v1/public/payments/card/callback",
    val approve_url: String = "https://vehnixauto.com",
    val cancel_url: String = "https://vehnixauto.com",
    val decline_url: String = "https://vehnixauto.com/contact"
)

data class TransactionCardRequest(
    @NotNull
    val deviseId: Long,
)
