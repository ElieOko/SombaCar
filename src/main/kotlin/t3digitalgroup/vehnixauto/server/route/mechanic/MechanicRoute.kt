package t3digitalgroup.vehnixauto.server.route.mechanic

import t3digitalgroup.vehnixauto.server.route.GlobalRoute

object MechanicScope {
    const val PROTECTED = "${GlobalRoute.PROTECT}/${MechanicFeatures.MECHANIC_PATH}"
}

object MechanicFeatures {
    const val MECHANIC_PATH = "mechanics"
}
