package t3digitalgroup.vehnixauto.server.app.report.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "listing_reports")
class ListingReportEntity(
    @Id
    @Column("id")
    val reportId: Long? = null,
    @Column("listing_type")
    val listingType: String,
    @Column("listing_id")
    val listingId: Long,
    @Column("reported_by")
    val reportedBy: Long,
    @Column("reason")
    val reason: String? = null,
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
