package emy.backend.barua.app.user.infrastructure.entities

import emy.backend.barua.app.user.domain.models.Account
import emy.backend.barua.app.user.domain.models.TypeAccount
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

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