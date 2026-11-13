package t3digitalgroup.vehnixauto.server.app.moto.domain.models

import t3digitalgroup.vehnixauto.server.utils.ItemCondition
import t3digitalgroup.vehnixauto.server.utils.ListingStatus
import t3digitalgroup.vehnixauto.server.utils.ListingType
import java.time.LocalDateTime

data class MotoListing(
    val listingId: Long? = null,
    val userId: Long,
    val motoModelId: Long,
    val listingType: String = ListingType.SALE.name,
    val year: Int? = null,
    val isElectric: Boolean = false,
    val mileageKm: Long? = null,
    val numberVin: String? = null,
    val condition: String = ItemCondition.USED.name,
    val plateNumber: String? = null,
    val color: String? = null,
    val engineCc: Int? = null,
    val price: String? = null,
    val devise: Long? = null,
    val rentPricePerDay: String? = null,
    val rentPricePerWeek: String? = null,
    val rentPricePerMonth: String? = null,
    val transmission: String? = null,
    val withDriver: Boolean = false,
    val driverUserId: Long? = null,
    val rentalAvailable: Boolean = true,
    val exchangeDescription: String? = null,
    val description: String? = null,
    val city: String? = null,
    val country: String = "Democratic Republic of the Congo",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val status: String = ListingStatus.ACTIVE.name,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val motoModel: MotoModel? = null,
    val images: List<MotoImage> = emptyList(),
    val documents: List<MotoDocument>? = null,
)
