package t3digitalgroup.vehnixauto.server.app.moto.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("moto_images")
class MotoImageEntity(
    @Id
    @Column("id")
    val id: Long? = null,
    @Column("moto_id")
    var motoId: Long? = null,
    @Column("name")
    var name: String,
    @Column("path")
    var path: String,
)
