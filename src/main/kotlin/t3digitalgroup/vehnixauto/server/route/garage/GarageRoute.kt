package t3digitalgroup.vehnixauto.server.route.garage

import t3digitalgroup.vehnixauto.server.route.GlobalRoute

object GarageScope {
    const val PUBLIC = "${GlobalRoute.PUBLIC}/${GarageFeatures.GARAGE_PATH}"
    const val PROTECTED = "${GlobalRoute.PROTECT}/${GarageFeatures.GARAGE_PATH}"
}

object GarageFeatures {
    const val GARAGE_PATH = "garages"
}
