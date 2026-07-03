package t3digitalgroup.vehnixauto.server.app.payment.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.DeviseType
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.StatusPayment
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.TypePayment
import java.time.LocalDate

@Table(name = "paiements")
class PaiementEntity(
    @Id
    @Column("id")
    val id: Long? = null,
    @Column("user_id")
    val userId: Long,
    @Column("reference")
    val reference: String,
    @Column("amount")
    val amount: String,
    @Column("devise")
    val devise: String = DeviseType.USD.name,
    @Column("description")
    val description: String?,
    @Column("type_payment")
    val typePayment: String = TypePayment.MOBILE_MONEY.name,
    @Column("status")
    var status: String = StatusPayment.PENDING.name,
    @Column("date_created")
    val dateCreated: LocalDate = LocalDate.now(),
    @Column("date_updated")
    val dateUpdated: LocalDate = LocalDate.now()
)
