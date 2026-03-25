package initializer.backend.spring.app.user.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "type_accounts")
class TypeAccountEntity(
    @Id
    @Column("id")
    val id: Long? = null,
    @Column("name")
    val name: String
)