package t3digitalgroup.vehnixauto.server.app.rental.domain.models

import jakarta.validation.constraints.NotNull
import t3digitalgroup.vehnixauto.server.utils.RentalDurationUnit
import t3digitalgroup.vehnixauto.server.utils.RentalRequestStatus
import t3digitalgroup.vehnixauto.server.utils.VehicleKind
import java.time.LocalDate
import java.time.LocalDateTime

data class RentalRequest(
    val id: Long? = null,
    val vehicleType: VehicleKind,
    val vehicleListingId: Long,
    val clientUserId: Long,
    val driverUserId: Long? = null,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val durationUnit: RentalDurationUnit = RentalDurationUnit.DAYS,
    val durationValue: Int = 1,
    val destinations: String? = null,
    val plannedRoute: String? = null,
    val motive: String? = null,
    val additionalInfo: String? = null,
    val quotedAmount: String? = null,
    val devise: String? = "USD",
    val paymentLink: String? = null,
    val paymentReference: String? = null,
    val status: RentalRequestStatus = RentalRequestStatus.PENDING,
    val supportThreadId: Long? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)

data class RentalRequestCreate(
    @field:NotNull
    val vehicleType: VehicleKind,
    @field:NotNull
    val vehicleListingId: Long,
    @field:NotNull
    val startDate: LocalDate,
    @field:NotNull
    val endDate: LocalDate,
    val durationUnit: RentalDurationUnit = RentalDurationUnit.DAYS,
    val durationValue: Int = 1,
    val destinations: String? = null,
    val plannedRoute: String? = null,
    val motive: String? = null,
    val additionalInfo: String? = null,
)

data class RentalQuoteRequest(
    val quotedAmount: String,
    val devise: String = "USD",
    val paymentLink: String? = null,
    val paymentReference: String? = null,
    val driverUserId: Long? = null,
    val status: RentalRequestStatus? = null,
)
