package t3digitalgroup.vehnixauto.server.app.tools.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("part_images")
class PartImageEntity(
    @Id
    @Column("id")
    val id: Long? = null,
    @Column("part_listing_id")
    var partListingId: Long? = null,
    @Column("name")
    var name: String,
    @Column("path")
    var path: String,
)
