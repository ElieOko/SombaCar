package t3digitalgroup.vehnixauto.server.utils

fun Int.generateOtp(): String {
    return (0 until this)
        .map { (0..9).random() }
        .joinToString("")
}
