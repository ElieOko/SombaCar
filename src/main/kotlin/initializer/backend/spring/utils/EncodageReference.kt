package initializer.backend.spring.utils

import java.security.MessageDigest
import java.util.UUID

//fun generateTransactionReference(): String {
//    val input = UUID.randomUUID().toString() + System.nanoTime()
//    val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
//    return bytes.joinToString("") { "%02x".format(it) }
//}

fun generateTransactionReference(): String {
    val prefix = "CasaNayo"
    val input = UUID.randomUUID().toString() + System.nanoTime()
    val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
    val hash = bytes.joinToString("") { "%02x".format(it) }

    return prefix + hash.take(20 - prefix.length)
}