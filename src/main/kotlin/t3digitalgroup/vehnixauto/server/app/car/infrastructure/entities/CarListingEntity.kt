package t3digitalgroup.vehnixauto.server.app.car.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.*
import t3digitalgroup.vehnixauto.server.utils.*
import java.time.*

@Table(name = "car_listings")
class CarListingEntity(
    @Id
    @Column("id")
    val listingId: Long? = null,
    @Column("user_id")
    val userId: Long,
    @Column("car_model_id")
    var carModelId: Long,
    @Column("listing_type")
    var listingType: String = ListingType.SALE.name,
    @Column("year")
    var year: Int? = null,
    @Column("is_electric")
    var isElectric: Boolean = false,
    @Column("mileage_km")
    var mileageKm: Long? = null,
    @Column("condition")
    var condition: String = ItemCondition.USED.name,
    @Column("plate_number")
    var plateNumber: String? = null,
    @Column("number_vin")
    var numberVin: String? = null,
    @Column("color")
    var color: String? = null,
    @Column("seats")
    var seats: Int? = null,
    @Column("price")
    var price: String? = null,
    @Column("devise_id")
    var deviseId: Long? = null,
    @Column("rent_price_per_day")
    var rentPricePerDay: String? = null,
    @Column("rent_price_per_week")
    var rentPricePerWeek: String? = null,
    @Column("rent_price_per_month")
    var rentPricePerMonth: String? = null,
    @Column("transmission")
    var transmission: String? = null,
    @Column("with_driver")
    var withDriver: Boolean = false,
    @Column("driver_user_id")
    var driverUserId: Long? = null,
    @Column("rental_available")
    var rentalAvailable: Boolean = true,
    @Column("exchange_description")
    var exchangeDescription: String? = null,
    @Column("description")
    var description: String? = null,
    @Column("city")
    var city: String? = null,
    @Column("country")
    var country: String = "Democratic Republic of the Congo",
    @Column("latitude")
    var latitude: Double? = null,
    @Column("longitude")
    var longitude: Double? = null,
    @Column("status")
    var status: String = ListingStatus.ACTIVE.name,
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column("updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
