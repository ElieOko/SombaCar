package t3digitalgroup.vehnixauto.server.app.report.infrastructure.mapper

import t3digitalgroup.vehnixauto.server.app.report.domain.models.ListingReport
import t3digitalgroup.vehnixauto.server.app.report.infrastructure.entities.ListingReportEntity

fun ListingReportEntity.toDomain() = ListingReport(
    reportId = this.reportId,
    listingType = this.listingType,
    listingId = this.listingId,
    reportedBy = this.reportedBy,
    reason = this.reason,
    createdAt = this.createdAt,
)

fun ListingReport.toEntity() = ListingReportEntity(
    reportId = this.reportId,
    listingType = this.listingType,
    listingId = this.listingId,
    reportedBy = this.reportedBy,
    reason = this.reason,
    createdAt = this.createdAt,
)
