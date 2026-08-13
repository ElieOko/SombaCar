package t3digitalgroup.vehnixauto.server.app.report.domain.models

import java.time.LocalDateTime

data class ListingReport(
    val reportId: Long? = null,
    val listingType: String,
    val listingId: Long,
    val reportedBy: Long,
    val reason: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)

data class ListingReportResult(
    val report: ListingReport,
    val reportCount: Int,
    val listingDeactivated: Boolean,
)
