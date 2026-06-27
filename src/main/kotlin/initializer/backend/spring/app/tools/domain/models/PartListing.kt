package initializer.backend.spring.app.tools.domain.models

import initializer.backend.spring.utils.ItemCondition
import initializer.backend.spring.utils.ListingStatus
import initializer.backend.spring.utils.ListingType
import java.time.LocalDateTime

data class PartListing(
    val partListingId: Long? = null,
    val userId: Long,
    val name: String,
    val partReference: String? = null,
    val brand: String? = null,
    val compatibleBrand: String? = null,
    val compatibleModel: String? = null,
    val listingType: String = ListingType.SALE.name,
    val condition: String = ItemCondition.USED.name,
    val price: String? = null,
    val rentPricePerDay: String? = null,
    val exchangeDescription: String? = null,
    val description: String? = null,
    val city: String? = null,
    val country: String = "Democratic Republic of the Congo",
    val status: String = ListingStatus.ACTIVE.name,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
