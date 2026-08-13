package t3digitalgroup.vehnixauto.server.utils

import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

data class GeoCoordinatesRequest(
    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    val latitude: Double,
    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    val longitude: Double,
)

fun validateGeoCoordinates(latitude: Double?, longitude: Double?) {
    if (latitude == null && longitude == null) return
    if (latitude == null || longitude == null) {
        throw ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Latitude et longitude doivent être fournies ensemble.",
        )
    }
    if (latitude !in -90.0..90.0) {
        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Latitude invalide.")
    }
    if (longitude !in -180.0..180.0) {
        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Longitude invalide.")
    }
}
