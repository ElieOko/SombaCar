package t3digitalgroup.vehnixauto.server.route.car

import t3digitalgroup.vehnixauto.server.route.GlobalRoute


object CarModelScope {
    const val PUBLIC = "${GlobalRoute.PUBLIC}/${CarFeatures.CAR_MODEL_PATH}"
    const val PROTECTED = "${GlobalRoute.PROTECT}/${CarFeatures.CAR_MODEL_PATH}"
    const val PRIVATE = "${GlobalRoute.PRIVATE}/${CarFeatures.CAR_MODEL_PATH}"
}

object CarListingScope {
    const val PUBLIC = "${GlobalRoute.PUBLIC}/${CarFeatures.CAR_LISTING_PATH}"
    const val PROTECTED = "${GlobalRoute.PROTECT}/${CarFeatures.CAR_LISTING_PATH}"
    const val PRIVATE = "${GlobalRoute.PRIVATE}/${CarFeatures.CAR_LISTING_PATH}"
}

object CarFeatures {
    const val CAR_MODEL_PATH = "cars/models"
    const val CAR_LISTING_PATH = "cars/listings"
}
