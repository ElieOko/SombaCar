package t3digitalgroup.vehnixauto.server.app.moto.domain.models.request

import jakarta.validation.constraints.*
import t3digitalgroup.vehnixauto.server.app.car.domain.models.request.AssignDriverRequest
import t3digitalgroup.vehnixauto.server.utils.*

data class MotoListingRequest(
    @NotNull
    val userId: Long,
    @NotNull
    val motoModelId: Long,
    @NotNull
    val listingType: ListingType,
    @Min(1900)
    val year: Int? = null,
    val isElectric: Boolean = false,
    @Min(0)
    val mileageKm: Long? = null,
    val condition: ItemCondition = ItemCondition.USED,
    val plateNumber: String? = null,
    val color: String? = null,
    val engineCc: Int? = null,
    val price: String? = null,
    val devise: Long? = null,
    val numberVin: String? = null,
    val rentPricePerDay: String? = null,
    val rentPricePerWeek: String? = null,
    val rentPricePerMonth: String? = null,
    val transmission: TransmissionType? = null,
    val withDriver: Boolean = false,
    val driverUserId: Long? = null,
    val rentalAvailable: Boolean = true,
    val exchangeDescription: String? = null,
    val description: String? = null,
    val city: String? = null,
    val country: String = "Democratic Republic of the Congo",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val documentIds: List<Long>? = null,
)

data class MotoListingAdminUpdateRequest(
    val motoModelId: Long? = null,
    val year: Int? = null,
    val clearYear: Boolean = false,
    val isElectric: Boolean? = null,
    val mileageKm: Long? = null,
    val clearMileage: Boolean = false,
    val condition: ItemCondition? = null,
    val plateNumber: String? = null,
    val color: String? = null,
    val engineCc: Int? = null,
    val price: String? = null,
    val devise: Long? = null,
    val numberVin: String? = null,
    val rentPricePerDay: String? = null,
    val rentPricePerWeek: String? = null,
    val rentPricePerMonth: String? = null,
    val transmission: TransmissionType? = null,
    val withDriver: Boolean? = null,
    val driverUserId: Long? = null,
    val clearDriver: Boolean = false,
    val rentalAvailable: Boolean? = null,
    val exchangeDescription: String? = null,
    val description: String? = null,
    val city: String? = null,
    val country: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val status: ListingStatus? = null,
    val documentIds: List<Long>? = null,
)

data class MotoModelAdminUpdateRequest(
    val brand: String? = null,
    val model: String? = null,
    val generation: String? = null,
    val bodyType: String? = null,
)

typealias MotoAssignDriverRequest = AssignDriverRequest
