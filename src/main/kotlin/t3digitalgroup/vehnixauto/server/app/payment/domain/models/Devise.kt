package t3digitalgroup.vehnixauto.server.app.payment.domain.models

data class Devise(
    val id: Long?,
    val name: String,
    val code: String,
    var tauxLocal: Double? = 0.0,
)

data class DeviseLocal(
    val tauxLocal: Double? = 0.0,
)
