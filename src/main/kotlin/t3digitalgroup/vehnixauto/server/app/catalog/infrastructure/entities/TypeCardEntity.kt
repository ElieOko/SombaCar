package t3digitalgroup.vehnixauto.server.app.catalog.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("type_cards")
class TypeCardEntity(
    @Id
    @Column("id")
    val typeCardId: Long? = null,
    @Column("name")
    var name: String,
)
