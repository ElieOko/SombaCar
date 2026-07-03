package t3digitalgroup.vehnixauto.server.app.tools.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import t3digitalgroup.vehnixauto.server.utils.ItemCondition
import t3digitalgroup.vehnixauto.server.utils.ListingStatus
import t3digitalgroup.vehnixauto.server.utils.ListingType
import java.time.LocalDateTime

@Table(name = "part_listings")
class PartListingEntity(
    @Id
    @Column("id")
    val partListingId: Long? = null,
    @Column("user_id")
    val userId: Long,
    @Column("name")
    val name: String,
    @Column("part_reference")
    val partReference: String? = null,
    @Column("brand")
    val brand: String? = null,
    @Column("compatible_brand")
    val compatibleBrand: String? = null,
    @Column("compatible_model")
    val compatibleModel: String? = null,
    @Column("listing_type")
    val listingType: String = ListingType.SALE.name,
    @Column("condition")
    val condition: String = ItemCondition.USED.name,
    @Column("price")
    val price: String? = null,
    @Column("rent_price_per_day")
    val rentPricePerDay: String? = null,
    @Column("exchange_description")
    val exchangeDescription: String? = null,
    @Column("description")
    val description: String? = null,
    @Column("city")
    val city: String? = null,
    @Column("country")
    val country: String = "Democratic Republic of the Congo",
    @Column("status")
    var status: String = ListingStatus.ACTIVE.name,
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column("updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
