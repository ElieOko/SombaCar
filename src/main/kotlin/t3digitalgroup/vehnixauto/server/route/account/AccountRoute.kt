package t3digitalgroup.vehnixauto.server.route.account

import t3digitalgroup.vehnixauto.server.route.GlobalRoute


object AccountScope{
    const val PUBLIC = "${GlobalRoute.PUBLIC}/${AccountFeatures.ACCOUNT_PATH}"
    const val PROTECTED = "${GlobalRoute.PROTECT}/${AccountFeatures.ACCOUNT_PATH}"
    const val PRIVATE = "${GlobalRoute.PRIVATE}/${AccountFeatures.ACCOUNT_PATH}"
}
object AccountTypeScope{
    const val PUBLIC = "${GlobalRoute.PUBLIC}/${AccountFeatures.ACCOUNT_TYPE_PATH}"
    const val PROTECTED = "${GlobalRoute.PROTECT}/${AccountFeatures.ACCOUNT_TYPE_PATH}"
    const val PRIVATE ="${GlobalRoute.PRIVATE}/${AccountFeatures.ACCOUNT_TYPE_PATH}"
}
object SchedulerScope{
    const val PUBLIC = "${GlobalRoute.PUBLIC}/${AccountFeatures.SCHEDULER}"
}
object AccountFeatures {
    const val ACCOUNT_PATH = "accounts"
    const val ACCOUNT_TYPE_PATH = "accounts/type"
    const val SCHEDULER = "scheduler/task"
}