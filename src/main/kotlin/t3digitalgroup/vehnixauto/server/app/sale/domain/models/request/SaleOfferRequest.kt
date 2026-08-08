package t3digitalgroup.vehnixauto.server.app.sale.domain.models.request

import jakarta.validation.constraints.NotBlank
import t3digitalgroup.vehnixauto.server.utils.OfferType

data class SaleOfferRequest(
    val offerType: OfferType,
    @NotBlank
    val title: String,
    val description: String? = null,
    @NotBlank
    val price: String,
    val devise: String = "USD",
    val linkedListingId: Long? = null,
)

data class SaleOfferUpdateRequest(
    val offerType: OfferType,
    @NotBlank
    val title: String,
    val description: String? = null,
    @NotBlank
    val price: String,
    val devise: String = "USD",
    val linkedListingId: Long? = null,
    val status: String = "ACTIVE",
)
