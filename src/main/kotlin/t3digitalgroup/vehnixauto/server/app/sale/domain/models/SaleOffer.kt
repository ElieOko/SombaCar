package t3digitalgroup.vehnixauto.server.app.sale.domain.models

import t3digitalgroup.vehnixauto.server.utils.OfferStatus
import t3digitalgroup.vehnixauto.server.utils.OfferType
import java.time.LocalDateTime

data class SaleOffer(
    val saleOfferId: Long? = null,
    val offerType: String = OfferType.PART.name,
    val title: String,
    val description: String? = null,
    val price: String,
    val devise: String = "USD",
    val linkedListingId: Long? = null,
    val status: String = OfferStatus.ACTIVE.name,
    val createdBy: Long? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)
