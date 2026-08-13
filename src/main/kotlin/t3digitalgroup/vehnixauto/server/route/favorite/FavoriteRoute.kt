package t3digitalgroup.vehnixauto.server.route.favorite

import t3digitalgroup.vehnixauto.server.route.GlobalRoute

object FavoriteScope {
    const val PROTECTED = "${GlobalRoute.PROTECT}/${FavoriteFeatures.FAVORITE_PATH}"
}

object FavoriteFeatures {
    const val FAVORITE_PATH = "favorites"
}
