package t3digitalgroup.vehnixauto.server.route.sale

import t3digitalgroup.vehnixauto.server.route.GlobalRoute

object SaleOfferScope {
    const val PUBLIC = "${GlobalRoute.PUBLIC}/${SaleFeatures.SALE_OFFER_PATH}"
    const val PROTECTED = "${GlobalRoute.PROTECT}/${SaleFeatures.SALE_OFFER_PATH}"
}

object SaleFeatures {
    const val SALE_OFFER_PATH = "sale-offers"
}
