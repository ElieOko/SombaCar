package t3digitalgroup.vehnixauto.server.route.admin

import t3digitalgroup.vehnixauto.server.route.GlobalRoute

object AdminScope {
    const val PROTECTED = "${GlobalRoute.PROTECT}/${AdminFeatures.ADMIN_PATH}"
}

object AdminFeatures {
    const val ADMIN_PATH = "admin"
}
