package t3digitalgroup.vehnixauto.server.route.payment

import t3digitalgroup.vehnixauto.server.route.GlobalRoute


object PaymentScope {
    const val PUBLIC = "${GlobalRoute.PUBLIC}/${PaymentFeatures.PAYMENT_PATH}"
    const val PROTECTED = "${GlobalRoute.PROTECT}/${PaymentFeatures.PAYMENT_PATH}"
    const val PRIVATE = "${GlobalRoute.PRIVATE}/${PaymentFeatures.PAYMENT_PATH}"
}

object DeviseScope {
    const val PUBLIC = "${GlobalRoute.PUBLIC}/${PaymentFeatures.DEVISE_PATH}"
    const val PROTECTED = "${GlobalRoute.PROTECT}/${PaymentFeatures.DEVISE_PATH}"
    const val PRIVATE = "${GlobalRoute.PRIVATE}/${PaymentFeatures.DEVISE_PATH}"
}

object PaymentFeatures {
    const val PAYMENT_PATH = "payments"
    const val DEVISE_PATH = "payments/devises"
}
