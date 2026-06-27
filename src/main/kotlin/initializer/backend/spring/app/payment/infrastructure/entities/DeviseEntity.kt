package initializer.backend.spring.app.payment.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "devises")
class DeviseEntity(
    @Id
    @Column("id")
    val id: Long? = null,
    @Column("name")
    val name: String,
    @Column("code")
    val code: String,
    @Column("taux_local")
    val tauxLocal: Double? = 22500.0
)
