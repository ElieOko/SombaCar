package t3digitalgroup.vehnixauto.server.app.user.domain.models

import jakarta.validation.constraints.*
import java.time.LocalDateTime

data class User(
    @Null
    val userId: Long? = null,
    @NotNull
    var firstName : String,
    @NotNull
    var lastName : String,
    @NotNull
    @field:NotBlank(message = "Le mot de passe est obligatoire")
    @field:Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    val password: String ="",
    @Null
    val email: String? = null,
    @Null
    val username: String? = null,
    val phone: String? = null,
    val city: String = "",
    @FutureOrPresent
    @Null
    val createdStart: LocalDateTime? = LocalDateTime.now()
)

data class UserUpdate(
    val email: String? = "",
    val phone: String? = "",
    val city: String = "",
    val country : String = "",
)

data class UserDto(
    val userId: Long? = null,
    var email: String? = null,
    val username: String,
    var phone: String?,
    var city: String,
    var firstName : String,
    var lastName : String,
    val isPremium : Boolean,
    var isCertified: Boolean
)

data class UserRequest(
    @field:NotBlank(message = "L'email est obligatoire")
    val email : String? = null,
    @NotNull(message = "Le mot de passe est obligatoire")
    @field:NotBlank(message = "Le mot de passe est obligatoire")
    @field:Size(min = 6, message = "Le mot de passe doit contenir au moins 8 caractères")
    val password : String,
    @NotNull
    val confirmPassword: String,
    val pseudo : String? = null,
    val phone : String? = null,
    @NotNull
    val city : String,
    @NotNull
    @field:NotBlank(message = "Le prénom est obligatoire")
    var firstName : String,
    @NotNull
    @field:NotBlank(message = "Le nom est obligatoire")
    var lastName : String,
)

data class UserAuthRequest(
//    val account : List<AccountRequest>?,
    val user : UserRequest
)

fun UserRequest.toDomain() = User(
    password = this.password,
    email = this.email,
    username = "@" + this.pseudo,
    phone = this.phone,
    city = this.city,
    firstName = this.firstName,
    lastName = this.lastName,
//    country = this.user.country,
)
data class RefreshRequest(
    val refreshToken: String
)

data class ImageUserRequest(
    @NotNull
    val image : String
)