package t3digitalgroup.vehnixauto.server.app.payment.domain.models

import com.fasterxml.jackson.annotation.JsonIgnore

data class ResponseTransaction(
    val code: String?,
    val message: String?,
    @JsonIgnore
    val orderNumber: String?
)

data class ResponseTransactionCard(
    val code: String?,
    val message: String?,
    @JsonIgnore
    val orderNumber: String?,
    val url: String?
)

data class TransactionCallBack(
    val code: String?,
    val reference: String?,
    val orderNumber: String?,
    val provider_reference: String?,
)
