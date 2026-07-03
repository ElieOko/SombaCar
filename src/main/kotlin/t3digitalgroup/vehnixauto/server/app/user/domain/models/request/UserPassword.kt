package t3digitalgroup.vehnixauto.server.app.user.domain.models.request

import jakarta.validation.constraints.*

data class UserPassword(
    @NotNull
    @field:NotBlank(message = "Le nouveau mot de passe est obligatoire")
    @field:Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    val newPassword : String,
)
data class CertificationState(
    @NotNull
    val state : Boolean = true
)