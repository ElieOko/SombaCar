package t3digitalgroup.vehnixauto.server.app.user.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import t3digitalgroup.vehnixauto.server.app.user.domain.models.Account
import t3digitalgroup.vehnixauto.server.app.user.domain.models.TypeAccount

@Table("accounts")
class AccountEntity(
    @Id
    @Column("id")
    val id: Long? = null,
    @Column("name")
    val name: String,
    @Column("type_account_id")
    val typeAccountId: Long
)
data class AccountDTO(
    val id : Long? = null,
    val name: String,
    val typeAccount : TypeAccount,
)
fun AccountEntity.toAccount(name: String) = TypeAccount(
    typeAccountId = this.typeAccountId,
    name = name,
)
fun AccountEntity.toDomain() = Account(id = this.id, name = this.name, typeAccountId = this.typeAccountId)