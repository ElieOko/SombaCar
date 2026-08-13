package t3digitalgroup.vehnixauto.server.app.report.domain.models.request

import jakarta.validation.constraints.NotNull
import t3digitalgroup.vehnixauto.server.utils.OfferType

data class ListingReportRequest(
    @NotNull
    val listingType: OfferType,
    @NotNull
    val listingId: Long,
    val reason: String? = null,
)
