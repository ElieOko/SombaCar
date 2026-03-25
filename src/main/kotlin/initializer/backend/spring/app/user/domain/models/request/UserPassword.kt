package initializer.backend.spring.app.user.domain.models.request

import jakarta.validation.constraints.*

data class UserPassword(
    @NotNull
    @field:NotBlank(message = "Le nouveau mot de passe est obligatoire")
    @field:Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    val newPassword : String,
    @NotNull
    val userId : Long

)
data class CertificationState(
    @NotNull
    val state : Boolean = true,
    val message : String?= null,
)