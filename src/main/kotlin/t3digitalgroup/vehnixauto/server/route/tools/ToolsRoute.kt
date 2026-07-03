package t3digitalgroup.vehnixauto.server.route.tools

import t3digitalgroup.vehnixauto.server.route.GlobalRoute

object PartListingScope {
    const val PUBLIC = "${GlobalRoute.PUBLIC}/${ToolsFeatures.PART_LISTING_PATH}"
    const val PROTECTED = "${GlobalRoute.PROTECT}/${ToolsFeatures.PART_LISTING_PATH}"
    const val PRIVATE = "${GlobalRoute.PRIVATE}/${ToolsFeatures.PART_LISTING_PATH}"
}

object ToolsFeatures {
    const val PART_LISTING_PATH = "parts/listings"
}
