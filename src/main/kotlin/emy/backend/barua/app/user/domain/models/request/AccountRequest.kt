package emy.backend.barua.app.user.domain.models.request

import jakarta.validation.constraints.NotNull

data class AccountRequest(
    @NotNull
    val typeAccount : Long
)
