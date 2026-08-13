package t3digitalgroup.vehnixauto.server.app.favorite.infrastructure.mapper

import t3digitalgroup.vehnixauto.server.app.favorite.domain.models.ListingFavorite
import t3digitalgroup.vehnixauto.server.app.favorite.infrastructure.entities.ListingFavoriteEntity

fun ListingFavoriteEntity.toDomain() = ListingFavorite(
    favoriteId = this.favoriteId,
    userId = this.userId,
    listingType = this.listingType,
    listingId = this.listingId,
    createdAt = this.createdAt,
)

fun ListingFavorite.toEntity() = ListingFavoriteEntity(
    favoriteId = this.favoriteId,
    userId = this.userId,
    listingType = this.listingType,
    listingId = this.listingId,
    createdAt = this.createdAt,
)
