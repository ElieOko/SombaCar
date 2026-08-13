package t3digitalgroup.vehnixauto.server.app.favorite.domain.models

import java.time.LocalDateTime

data class ListingFavorite(
    val favoriteId: Long? = null,
    val userId: Long,
    val listingType: String,
    val listingId: Long,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
