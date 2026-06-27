package initializer.backend.spring.app.car.domain.models

import initializer.backend.spring.utils.ItemCondition
import initializer.backend.spring.utils.ListingStatus
import initializer.backend.spring.utils.ListingType
import java.time.LocalDateTime

data class CarListing(
    val listingId: Long? = null,
    val userId: Long,
    val carModelId: Long,
    val listingType: String = ListingType.SALE.name,
    val year: Int,
    val isElectric: Boolean = false,
    val mileageKm: Long = 0,
    val condition: String = ItemCondition.USED.name,
    val plateNumber: String? = null,
    val color: String? = null,
    val seats: Int? = null,
    val price: String? = null,
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
