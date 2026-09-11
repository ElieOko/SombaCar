package t3digitalgroup.vehnixauto.server.security

object JwtUserIdSupport {
    fun parseSubject(subject: String): Long =
        subject.toLongOrNull() ?: subject.toLong(16)

    fun formatUserId(userId: Long): String = userId.toString(16)
}
