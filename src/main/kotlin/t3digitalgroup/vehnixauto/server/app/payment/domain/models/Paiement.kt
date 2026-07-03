package t3digitalgroup.vehnixauto.server.app.payment.domain.models

import t3digitalgroup.vehnixauto.server.app.user.domain.models.UserDto
import java.time.LocalDate

data class Paiement(
    val id: Long? = null,
    val userId: Long,
    val reference: String,
    val amount: String,
    val devise: String = DeviseType.USD.name,
    val description: String?,
    val typePayment: String = TypePayment.MOBILE_MONEY.name,
    var status: String = StatusPayment.PENDING.name,
    val dateCreated: LocalDate = LocalDate.now(),
    val dateUpdated: LocalDate = LocalDate.now()
)

data class PaymentDTO(
    val payment: List<Paiement>,
    val user: UserDto
)
