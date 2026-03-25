package server.web.casa.app.user.domain.model

import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Null
import jakarta.validation.constraints.Size
import server.web.casa.app.user.domain.model.request.AccountRequest
import java.time.LocalDateTime

data class User(
    @Null
    val userId: Long = 0,
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
    @NotNull
    val country : String,
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
    var phone: String,
    var city: String,
    var country : String? = "Democratic Republic of the Congo",
    val isPremium : Boolean,
    var isCertified: Boolean
)

data class UserRequest(
//    @field:NotBlank(message = "Le phone est obligatoire")
//    @field:Size(min = 8, message = "Ce numero est invalide car il ne respecte pas le nommage")
    val phone : String? = null,
    @NotNull
    @field:NotBlank(message = "Le mot de passe est obligatoire")
    @field:Size(min = 6, message = "Le mot de passe doit contenir au moins 8 caractères")
    val password : String,
    val email : String? = null,
    val username : String? = null,
    @NotNull
    val city : String,
    @NotNull
    val country : String
)

data class UserAuthRequest(val account : List<AccountRequest>, val user : UserRequest)

fun UserAuthRequest.toDomain() = User(
    password = this.user.password,
    email = this.user.email,
    username = "@"+this.user.username,
    phone = this.user.phone,
    city = this.user.city,
    country = this.user.country,
)
data class RefreshRequest(
    val refreshToken: String
)

data class ImageUserRequest(
    @NotNull
    val image : String
)