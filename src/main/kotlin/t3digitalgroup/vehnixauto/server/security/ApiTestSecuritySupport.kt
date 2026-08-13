package t3digitalgroup.vehnixauto.server.security

object ApiTestSecuritySupport {
    const val TEST_USER_ID = 1L

    val temporaryPublicPatterns = listOf(
        "/api/v1/protected/cart/**",
        "/api/v1/protected/favorites/**",
        "/api/v1/protected/listing-reports/**",
        "/api/v1/protected/garages/**",
        "/api/v1/protected/subscriptions/**",
        "/api/v1/protected/payments/subscription/**",
        "/api/v1/protected/mechanics/**",
    )

    fun testUserPrincipalName(): String = TEST_USER_ID.toString(16)
}
