package t3digitalgroup.vehnixauto.server.route.subscription

import t3digitalgroup.vehnixauto.server.route.GlobalRoute

object SubscriptionScope {
    const val PUBLIC = "${GlobalRoute.PUBLIC}/${SubscriptionFeatures.SUBSCRIPTION_PATH}"
    const val PROTECTED = "${GlobalRoute.PROTECT}/${SubscriptionFeatures.SUBSCRIPTION_PATH}"
}

object SubscriptionFeatures {
    const val SUBSCRIPTION_PATH = "subscriptions"
    const val PLAN_PATH = "subscription-plans"
}

object SubscriptionPlanScope {
    const val PUBLIC = "${GlobalRoute.PUBLIC}/${SubscriptionFeatures.PLAN_PATH}"
    const val PROTECTED = "${GlobalRoute.PROTECT}/${SubscriptionFeatures.PLAN_PATH}"
}
