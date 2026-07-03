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
