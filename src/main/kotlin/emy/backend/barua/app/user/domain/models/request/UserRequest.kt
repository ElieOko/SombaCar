package emy.backend.barua.app.user.domain.models.request

import jakarta.validation.constraints.*

data class UserRequestChange(
    @NotNull
    val email : String,
    @NotNull
    val username : String,
    @NotNull
    val city  : String,
)

data class IdentifiantRequest(
    @NotNull
    val identifier : String,
)

data class VerifyRequest(
    @NotNull
    val identifier : String,
    @NotNull
    val code : String,
)