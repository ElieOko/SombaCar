package t3digitalgroup.vehnixauto.server.app.user.domain.models.request

import jakarta.validation.constraints.*

data class UserRequestChange(
    @NotNull
    val email : String,
    @NotNull
    val pseudo : String = "",
    @NotNull
    val phone : String = "",
    @NotNull
    val city : String = "",
    @NotNull
    @field:NotBlank(message = "Le prénom est obligatoire")
    var firstName : String,
    @NotNull
    @field:NotBlank(message = "Le nom est obligatoire")
    var lastName : String,
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