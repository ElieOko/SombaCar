package t3digitalgroup.vehnixauto.server.route.catalog

import t3digitalgroup.vehnixauto.server.route.GlobalRoute

object TypeCardScope {
    const val PUBLIC = "${GlobalRoute.PUBLIC}/${CatalogFeatures.TYPE_CARD_PATH}"
    const val PROTECTED = "${GlobalRoute.PROTECT}/${CatalogFeatures.TYPE_CARD_PATH}"
}

object CatalogFeatures {
    const val TYPE_CARD_PATH = "catalog/type-cards"
}
