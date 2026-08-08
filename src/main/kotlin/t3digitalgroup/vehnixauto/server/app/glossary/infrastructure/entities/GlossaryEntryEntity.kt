package t3digitalgroup.vehnixauto.server.app.glossary.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "glossary_entries")
class GlossaryEntryEntity(
    @Id
    @Column("id")
    val glossaryId: Long? = null,
    @Column("official_name")
    var officialName: String,
    @Column("category")
    var category: String,
    @Column("local_names")
    var localNames: String = "[]",
    @Column("description")
    var description: String? = null,
    @Column("wear_signs")
    var wearSigns: String? = null,
    @Column("tips")
    var tips: String? = null,
    @Column("created_by")
    val createdBy: Long? = null,
    @Column("status")
    var status: String = "ACTIVE",
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column("updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)
