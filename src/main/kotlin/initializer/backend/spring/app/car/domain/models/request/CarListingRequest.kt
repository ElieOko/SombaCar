package initializer.backend.spring.app.car.domain.models.request

import initializer.backend.spring.utils.ItemCondition
import initializer.backend.spring.utils.ListingType
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class CarListingRequest(
    @NotNull
    val userId: Long,
    @NotNull
    val carModelId: Long,
    @NotNull
    val listingType: ListingType,
    @NotNull
    @Min(1900)
    val year: Int,
    val isElectric: Boolean = false,
    @Min(0)
    val mileageKm: Long = 0,
    val condition: ItemCondition = ItemCondition.USED,
    val plateNumber: String? = null,
    val color: String? = null,
    val seats: Int? = null,
    val price: String? = null,
    val rentPricePerDay: String? = null,
    val exchangeDescription: String? = null,
    val description: String? = null,
    val city: String? = null,
    val country: String = "Democratic Republic of the Congo"
)
