package t3digitalgroup.vehnixauto.server.route.cart

import t3digitalgroup.vehnixauto.server.route.GlobalRoute

object CartScope {
    const val PROTECTED = "${GlobalRoute.PROTECT}/${CartFeatures.CART_PATH}"
}

object CartFeatures {
    const val CART_PATH = "cart"
}
