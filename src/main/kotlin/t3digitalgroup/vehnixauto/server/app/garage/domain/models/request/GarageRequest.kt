package t3digitalgroup.vehnixauto.server.app.garage.domain.models.request

import jakarta.validation.constraints.NotBlank
import t3digitalgroup.vehnixauto.server.utils.GarageType

data class GarageRequest(
    @NotBlank
    val name: String,
    val description: String? = null,
    val address: String? = null,
    val city: String? = null,
    val country: String = "Democratic Republic of the Congo",
    val phone: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val garageType: GarageType = GarageType.CAR,
)

data class GarageUpdateRequest(
    @NotBlank
    val name: String,
    val description: String? = null,
    val address: String? = null,
    val city: String? = null,
    val country: String = "Democratic Republic of the Congo",
    val phone: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val garageType: GarageType = GarageType.CAR,
)
