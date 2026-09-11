package t3digitalgroup.vehnixauto.server.route.message

import t3digitalgroup.vehnixauto.server.route.GlobalRoute


object SupportThreadScope {
    const val PUBLIC = "${GlobalRoute.PUBLIC}/${MessageFeatures.SUPPORT_THREAD_PATH}"
    const val PROTECTED = "${GlobalRoute.PROTECT}/${MessageFeatures.SUPPORT_THREAD_PATH}"
    const val PRIVATE = "${GlobalRoute.PRIVATE}/${MessageFeatures.SUPPORT_THREAD_PATH}"
}

object MessageScope {
    const val PUBLIC = "${GlobalRoute.PUBLIC}/${MessageFeatures.MESSAGE_PATH}"
    const val PROTECTED = "${GlobalRoute.PROTECT}/${MessageFeatures.MESSAGE_PATH}"
    const val PRIVATE = "${GlobalRoute.PRIVATE}/${MessageFeatures.MESSAGE_PATH}"
}

object MessageFeatures {
    const val SUPPORT_THREAD_PATH = "support/threads"
    const val MESSAGE_PATH = "support/messages"
    const val CAR_LISTING_THREAD_PATH = "cars/listings/threads"
    const val CAR_LISTING_MESSAGE_PATH = "cars/listings/messages"
    const val MOTO_LISTING_THREAD_PATH = "motos/listings/threads"
    const val MOTO_LISTING_MESSAGE_PATH = "motos/listings/messages"
    const val PART_LISTING_THREAD_PATH = "parts/listings/threads"
    const val PART_LISTING_MESSAGE_PATH = "parts/listings/messages"
}

object CarListingThreadScope {
    const val PUBLIC = "${GlobalRoute.PUBLIC}/${MessageFeatures.CAR_LISTING_THREAD_PATH}"
    const val PROTECTED = "${GlobalRoute.PROTECT}/${MessageFeatures.CAR_LISTING_THREAD_PATH}"
    const val PRIVATE = "${GlobalRoute.PRIVATE}/${MessageFeatures.CAR_LISTING_THREAD_PATH}"
}

object CarListingMessageScope {
    const val PUBLIC = "${GlobalRoute.PUBLIC}/${MessageFeatures.CAR_LISTING_MESSAGE_PATH}"
    const val PROTECTED = "${GlobalRoute.PROTECT}/${MessageFeatures.CAR_LISTING_MESSAGE_PATH}"
    const val PRIVATE = "${GlobalRoute.PRIVATE}/${MessageFeatures.CAR_LISTING_MESSAGE_PATH}"
}

object MotoListingThreadScope {
    const val PUBLIC = "${GlobalRoute.PUBLIC}/${MessageFeatures.MOTO_LISTING_THREAD_PATH}"
    const val PROTECTED = "${GlobalRoute.PROTECT}/${MessageFeatures.MOTO_LISTING_THREAD_PATH}"
    const val PRIVATE = "${GlobalRoute.PRIVATE}/${MessageFeatures.MOTO_LISTING_THREAD_PATH}"
}

object MotoListingMessageScope {
    const val PUBLIC = "${GlobalRoute.PUBLIC}/${MessageFeatures.MOTO_LISTING_MESSAGE_PATH}"
    const val PROTECTED = "${GlobalRoute.PROTECT}/${MessageFeatures.MOTO_LISTING_MESSAGE_PATH}"
    const val PRIVATE = "${GlobalRoute.PRIVATE}/${MessageFeatures.MOTO_LISTING_MESSAGE_PATH}"
}

object PartListingThreadScope {
    const val PUBLIC = "${GlobalRoute.PUBLIC}/${MessageFeatures.PART_LISTING_THREAD_PATH}"
    const val PROTECTED = "${GlobalRoute.PROTECT}/${MessageFeatures.PART_LISTING_THREAD_PATH}"
    const val PRIVATE = "${GlobalRoute.PRIVATE}/${MessageFeatures.PART_LISTING_THREAD_PATH}"
}

object PartListingMessageScope {
    const val PUBLIC = "${GlobalRoute.PUBLIC}/${MessageFeatures.PART_LISTING_MESSAGE_PATH}"
    const val PROTECTED = "${GlobalRoute.PROTECT}/${MessageFeatures.PART_LISTING_MESSAGE_PATH}"
    const val PRIVATE = "${GlobalRoute.PRIVATE}/${MessageFeatures.PART_LISTING_MESSAGE_PATH}"
}
