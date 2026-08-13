package t3digitalgroup.vehnixauto.server.app.favorite.domain.models.request

import jakarta.validation.constraints.NotNull
import t3digitalgroup.vehnixauto.server.utils.OfferType

data class ListingFavoriteRequest(
    @NotNull
    val listingType: OfferType,
    @NotNull
    val listingId: Long,
)
