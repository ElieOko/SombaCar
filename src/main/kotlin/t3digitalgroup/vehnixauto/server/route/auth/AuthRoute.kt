package t3digitalgroup.vehnixauto.server.route.auth

import t3digitalgroup.vehnixauto.server.route.GlobalRoute


object AuthRoute {
    const val REGISTER = "${GlobalRoute.AUTH}/${AuthFeatures.REGISTER_PATH}"
    const val LOGIN = "${GlobalRoute.AUTH}/${AuthFeatures.LOGIN_PATH}"
    const val RESET_PASSWORD = "${GlobalRoute.AUTH}/${AuthFeatures.PASSWORD_RESET_PATH}"
}

object AuthFeatures{
   const val REGISTER_PATH = "register"
   const val LOGIN_PATH = "login"
   const val PASSWORD_RESET_PATH = "password/reset"
}