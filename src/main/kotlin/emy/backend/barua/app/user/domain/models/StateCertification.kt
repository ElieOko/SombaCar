package server.web.casa.app.user.domain.model

import org.jetbrains.annotations.NotNull

data class StateCertification(
    @NotNull
    val userId : Long,
    @NotNull
    val isCertified : Boolean
)
