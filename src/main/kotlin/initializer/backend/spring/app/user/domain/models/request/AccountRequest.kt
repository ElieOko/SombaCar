package initializer.backend.spring.app.user.domain.models.request

import jakarta.validation.constraints.NotNull

data class AccountRequest(
    @NotNull
    val typeAccount : Long
)
