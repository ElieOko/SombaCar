package t3digitalgroup.vehnixauto.server.app.moto.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import t3digitalgroup.vehnixauto.server.utils.ItemCondition
import t3digitalgroup.vehnixauto.server.utils.ListingStatus
import t3digitalgroup.vehnixauto.server.utils.ListingType
import java.time.LocalDateTime

@Table(name = "moto_listings")
class MotoListingEntity(
    @Id
    @Column("id")
    val listingId: Long? = null,
    @Column("user_id")
    val userId: Long,
    @Column("moto_model_id")
    val motoModelId: Long,
    @Column("listing_type")
    val listingType: String = ListingType.SALE.name,
    @Column("year")
    val year: Int,
    @Column("is_electric")
    val isElectric: Boolean = false,
    @Column("mileage_km")
    val mileageKm: Long = 0,
    @Column("condition")
    val condition: String = ItemCondition.USED.name,
    @Column("plate_number")
    val plateNumber: String? = null,
    @Column("number_vin")
    val numberVin: String? = null,
    @Column("color")
    val color: String? = null,
    @Column("engine_cc")
    val engineCc: Int? = null,
    @Column("price")
    val price: String? = null,
    @Column("devise_id")
    val deviseId: Long? = null,
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
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)
