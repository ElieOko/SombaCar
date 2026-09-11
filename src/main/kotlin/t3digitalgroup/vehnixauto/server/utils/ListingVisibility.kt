package t3digitalgroup.vehnixauto.server.utils

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

fun ListingStatus.isPubliclyVisible(): Boolean = this == ListingStatus.ACTIVE

fun ensurePubliclyVisible(status: String) {
    val listingStatus = runCatching { ListingStatus.valueOf(status) }.getOrNull()
    if (listingStatus?.isPubliclyVisible() != true) {
        throw ResponseStatusException(HttpStatus.NOT_FOUND, "Annonce introuvable.")
    }
}
