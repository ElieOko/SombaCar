package t3digitalgroup.vehnixauto.server.app.car.domain.models

import t3digitalgroup.vehnixauto.server.utils.*
import java.time.LocalDateTime

data class CarListing(
    val listingId: Long? = null,
    val userId: Long,
    val carModelId: Long,
    val listingType: String = ListingType.SALE.name,
    val year: Int,
    val isElectric: Boolean = false,
    val mileageKm: Long = 0,
    val numberVin: String? = null,
    val condition: String = ItemCondition.USED.name,
    val plateNumber: String? = null,
    val color: String? = null,
    val seats: Int? = null,
    val price: String? = null,
    val devise: Long? = null,
    val rentPricePerDay: String? = null,
    val exchangeDescription: String? = null,
    val description: String? = null,
    val city: String? = null,
    val country: String = "Democratic Republic of the Congo",
    val status: String = ListingStatus.ACTIVE.name,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val carModel: CarModel? = null
)
