package t3digitalgroup.vehnixauto.server.route.notification

import t3digitalgroup.vehnixauto.server.route.GlobalRoute


object NotificationScope {
    const val PUBLIC = "${GlobalRoute.PUBLIC}/${NotificationFeatures.NOTIFICATION_PATH}"
    const val PROTECTED = "${GlobalRoute.PROTECT}/${NotificationFeatures.NOTIFICATION_PATH}"
    const val PRIVATE = "${GlobalRoute.PRIVATE}/${NotificationFeatures.NOTIFICATION_PATH}"
}

object NotificationFeatures {
    const val NOTIFICATION_PATH = "notifications"
}
