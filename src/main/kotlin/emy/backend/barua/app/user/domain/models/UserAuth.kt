package server.web.casa.app.user.domain.model

import jakarta.validation.constraints.*

data class UserAuth(
    @NotNull
//    @field:NotBlank(message = "L'identifiant est obligatoire")
    @field:Size(min = 4, message = "Identifiant non valide")
    val identifiant : String,
    @NotNull
    @field:NotBlank(message = "Le mot de passe est obligatoire")
    @field:Size(min = 4, message = "Le mot de passe doit contenir au moins 4 caractères")
    val password : String
)
