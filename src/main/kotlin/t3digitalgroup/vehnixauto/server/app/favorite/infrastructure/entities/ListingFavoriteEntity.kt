package t3digitalgroup.vehnixauto.server.app.favorite.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "listing_favorites")
class ListingFavoriteEntity(
    @Id
    @Column("id")
    val favoriteId: Long? = null,
    @Column("user_id")
    val userId: Long,
    @Column("listing_type")
    val listingType: String,
    @Column("listing_id")
    val listingId: Long,
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
