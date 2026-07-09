package t3digitalgroup.vehnixauto.server.route.moto

import t3digitalgroup.vehnixauto.server.route.GlobalRoute

object MotoModelScope {
    const val PUBLIC = "${GlobalRoute.PUBLIC}/${MotoFeatures.MOTO_MODEL_PATH}"
    const val PROTECTED = "${GlobalRoute.PROTECT}/${MotoFeatures.MOTO_MODEL_PATH}"
    const val PRIVATE = "${GlobalRoute.PRIVATE}/${MotoFeatures.MOTO_MODEL_PATH}"
}

object MotoListingScope {
    const val PUBLIC = "${GlobalRoute.PUBLIC}/${MotoFeatures.MOTO_LISTING_PATH}"
    const val PROTECTED = "${GlobalRoute.PROTECT}/${MotoFeatures.MOTO_LISTING_PATH}"
    const val PRIVATE = "${GlobalRoute.PRIVATE}/${MotoFeatures.MOTO_LISTING_PATH}"
}

object MotoFeatures {
    const val MOTO_MODEL_PATH = "motos/models"
    const val MOTO_LISTING_PATH = "motos/listings"
}
