package t3digitalgroup.vehnixauto.server.app.user.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import t3digitalgroup.vehnixauto.server.app.user.domain.models.AccountUser

@Table(name = "account_users")
class AccountUserEntity(
    @Id
    @Column("id")
    val id: Long? = null,
    @Column("account_id")
    val accountId: Long,
    @Column("user_id")
    val userId: Long
)

fun AccountUserEntity.toDomain() = AccountUser(
    id = this.id,
    accountId = this.accountId,
    userId = this.userId,
)