package initializer.backend.spring.app.tools.domain.models.request

import initializer.backend.spring.utils.ItemCondition
import initializer.backend.spring.utils.ListingType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class PartListingRequest(
    @NotNull
    val userId: Long,
    @NotBlank
    val name: String,
    val partReference: String? = null,
    val brand: String? = null,
    val compatibleBrand: String? = null,
    val compatibleModel: String? = null,
    @NotNull
    val listingType: ListingType,
    val condition: ItemCondition = ItemCondition.USED,
    val price: String? = null,
    val rentPricePerDay: String? = null,
    val exchangeDescription: String? = null,
    val description: String? = null,
    val city: String? = null,
    val country: String = "Democratic Republic of the Congo"
)
