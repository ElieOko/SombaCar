package t3digitalgroup.vehnixauto.server.app.glossary.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("glossary_files")
class GlossaryFileEntity(
    @Id
    @Column("id")
    val glossaryFileId: Long? = null,
    @Column("glossary_id")
    var glossaryId: Long? = null,
    @Column("name")
    var name: String,
    @Column("path")
    var path: String,
    @Column("mime_type")
    var mimeType: String? = null,
)
