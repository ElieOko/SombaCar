package t3digitalgroup.vehnixauto.server.app.moto.domain.models

import t3digitalgroup.vehnixauto.server.app.car.domain.models.Document
import t3digitalgroup.vehnixauto.server.utils.ItemCondition
import t3digitalgroup.vehnixauto.server.utils.ListingStatus
import t3digitalgroup.vehnixauto.server.utils.ListingType
import java.time.LocalDateTime

data class MotoListing(
    val listingId: Long? = null,
    val userId: Long,
    val motoModelId: Long,
    val listingType: String = ListingType.SALE.name,
    val year: Int,
    val isElectric: Boolean = false,
    val mileageKm: Long = 0,
    val numberVin: String? = null,
    val condition: String = ItemCondition.USED.name,
    val plateNumber: String? = null,
    val color: String? = null,
    val engineCc: Int? = null,
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
    val motoModel: MotoModel? = null,
    val images: List<MotoImage> = emptyList(),
    val documents: List<MotoDocument>? = null,
)
