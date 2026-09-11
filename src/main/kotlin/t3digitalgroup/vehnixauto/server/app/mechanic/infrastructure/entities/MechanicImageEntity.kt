package t3digitalgroup.vehnixauto.server.app.mechanic.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("mechanic_images")
class MechanicImageEntity(
    @Id
    @Column("id")
    val id: Long? = null,
    @Column("mechanic_id")
    var mechanicId: Long? = null,
    @Column("name")
    var name: String,
    @Column("path")
    var path: String,
)
