package t3digitalgroup.vehnixauto.server.app.delivery.domain.models

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import t3digitalgroup.vehnixauto.server.utils.DeliveryStatus
import java.time.LocalDateTime

data class PartDelivery(
    val id: Long? = null,
    val partListingId: Long? = null,
    val buyerUserId: Long,
    val sellerUserId: Long? = null,
    val courierUserId: Long? = null,
    val deliveryAddress: String,
    val city: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val notes: String? = null,
    val paymentReference: String? = null,
    val status: DeliveryStatus = DeliveryStatus.PENDING,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)

data class PartDeliveryCreate(
    val partListingId: Long? = null,
    @field:NotBlank
    val deliveryAddress: String,
    val city: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val notes: String? = null,
    val paymentReference: String? = null,
)

data class PartDeliveryAssignRequest(
    @field:NotNull
    val courierUserId: Long,
)

data class PartDeliveryTrackingRequest(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val tag: String? = "Depart",
    val isActive: Boolean = true,
    @field:NotBlank
    val message: String,
)

data class PartDeliveryTrackingEvent(
    val id: Long? = null,
    val deliveryId: Long,
    val userId: Long? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val tag: String = "Depart",
    val isActive: Boolean = true,
    val message: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
