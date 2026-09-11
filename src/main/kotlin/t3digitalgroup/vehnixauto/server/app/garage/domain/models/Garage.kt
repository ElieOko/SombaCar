package t3digitalgroup.vehnixauto.server.app.garage.domain.models

import java.time.LocalDateTime

data class Garage(
    val garageId: Long? = null,
    val userId: Long,
    val name: String,
    val description: String? = null,
    val address: String? = null,
    val city: String? = null,
    val country: String = "Democratic Republic of the Congo",
    val phone: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val garageType: String,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val images: List<GarageImage> = emptyList(),
)
