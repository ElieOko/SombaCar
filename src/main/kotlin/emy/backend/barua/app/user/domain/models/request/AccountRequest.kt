package server.web.casa.app.user.domain.model.request

import jakarta.validation.constraints.NotNull

data class AccountRequest(
    @NotNull
    val typeAccount : Long
)
