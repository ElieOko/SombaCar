package initializer.backend.spring.app.user.application.services

import initializer.backend.spring.adaptater.provider.twilio.TwilioService
import initializer.backend.spring.app.user.domain.models.AccountUser
import initializer.backend.spring.app.user.domain.models.User
import initializer.backend.spring.app.user.domain.models.UserDto
import initializer.backend.spring.app.user.domain.models.UserFullDTO
import initializer.backend.spring.app.user.domain.models.request.AccountRequest
import initializer.backend.spring.app.user.domain.models.request.VerifyRequest
import initializer.backend.spring.app.user.infrastructure.entities.AccountDTO
import initializer.backend.spring.app.user.infrastructure.entities.UserEntity
import initializer.backend.spring.app.user.infrastructure.mapper.toDomain
import initializer.backend.spring.app.user.infrastructure.repositories.RefreshTokenRepository
import initializer.backend.spring.app.user.infrastructure.repositories.UserRepository
import initializer.backend.spring.security.HashEncoder
import initializer.backend.spring.security.JwtService
import initializer.backend.spring.utils.Mode
import initializer.backend.spring.utils.isEmailValid
import initializer.backend.spring.utils.normalizeAndValidatePhoneNumberUniversal
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.slf4j.*
import org.springframework.context.annotation.*
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.*
import org.springframework.web.server.*
import java.security.*
import java.util.*
import kotlin.time.*
//sudo docker run --name casa-db -e POSTGRES_PASSWORD=root -e POSTGRES_DB=testdb e- POSTGRES_USERNAME=postgres -p 5434:5432 -d postgres
//https://www.digitalocean.com/community/tutorials/how-to-install-and-use-docker-on-ubuntu-22-04
@Service
@Profile(Mode.DEV)
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val hashEncoder: HashEncoder,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val twilio : TwilioService,
    private val serviceMultiAccount: AccountUserService,
    private val typeAccountService: TypeAccountService,
    private val accountService: AccountService,
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    data class TokenPair(
        val accessToken: String,
        val refreshToken: String
    )
    @OptIn(ExperimentalTime::class)
    suspend fun register(user: User, accountItems: List<AccountRequest>): Pair<UserDto?, String> {
        var phone = normalizeAndValidatePhoneNumberUniversal(user.phone)
        var state = false
            if (user.phone != null) {
                if (user.phone.isNotEmpty()){
                    phone =  normalizeAndValidatePhoneNumberUniversal(user.phone) ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce numero n'est pas valide.")
                    if(userRepository.findByPhoneOrEmail(phone) != null) throw ResponseStatusException(HttpStatus.CONFLICT, "Ce numéro de téléphone est déjà associé à un compte existant.")
                    state = true
                }
            }
        if (user.email != null){
            if(user.email.isNotEmpty()){
                if(userRepository.findByPhoneOrEmail(user.email) != null) throw ResponseStatusException(HttpStatus.CONFLICT, "Cette adresse mail est déjà associé à un compte existant.")
                state = true
            }
        }
        if (!state) throw ResponseStatusException(HttpStatus.CONFLICT, "Vous devez renseigner l'email ou le phone.")
        val entity = UserEntity(
            password = hashEncoder.encode(user.password),
            email = user.email,
            username = user.username,
            phone = phone,
            city = user.city,
            country = user.country
        )
        log.info("Creating user ${user.userId}")
        val savedEntity = userRepository.save(entity)
        accountItems.forEach {
            serviceMultiAccount.save(AccountUser(userId = savedEntity.userId!!, accountId = it.typeAccount))
        }

        val newAccessToken = jwtService.generateAccessToken(savedEntity.userId!!.toHexString())
        val userData : UserDto = savedEntity.toDomain()
        val result = Pair(userData,newAccessToken)
        return result
    }
    suspend fun login(identifier: String, password: String): Pair<TokenPair, UserFullDTO>  =
        coroutineScope {
            var validIdentifier = normalizeAndValidatePhoneNumberUniversal(identifier)
            if (isEmailValid(identifier)) validIdentifier = identifier
            val user = userRepository.findByPhoneOrEmail(validIdentifier.toString()) ?: throw ResponseStatusException(HttpStatusCode.valueOf(403), "Invalid credentials.")
            if(!hashEncoder.matches(password, user.password.toString())) throw ResponseStatusException(HttpStatusCode.valueOf(403), "Invalid credentials.")
            log.info("Logging into user ${user.userId}")
            val newAccessToken = jwtService.generateAccessToken(user.userId!!.toHexString())
            val newRefreshToken = jwtService.generateRefreshToken(user.userId.toHexString())
            val accounts = serviceMultiAccount.getAll().filter { it.userId == user.userId }.toList()
            val accountMultiple: List<AccountDTO> =  accounts.map {
                val data = accountService.findByIdAccount(it.accountId)
                AccountDTO(id = data.id, name = data.name, typeAccount = typeAccountService.findByIdTypeAccount(data.typeAccountId))
            }.toList()
            val profile = userRepository.findById(user.userId)
//            storeRefreshToken(user.userId, newRefreshToken)
            val result = Pair(
                TokenPair(accessToken = newAccessToken, refreshToken = newRefreshToken),
                UserFullDTO(user.toDomain(), accountMultiple)
            )
            result
     }
    suspend fun generateOTP(identifier: String): Triple<String?, String, String> {
       var validIdentifier = normalizeAndValidatePhoneNumberUniversal(identifier)
       if (isEmailValid(identifier)) validIdentifier = identifier
       userRepository.findByPhoneOrEmail(validIdentifier.toString()) ?: throw ResponseStatusException(HttpStatusCode.valueOf(403), "Idenfiant invalide.")
       val status = if (isEmailValid(identifier)) twilio.generateVerifyOTP(identifier, channel = "email") else twilio.generateVerifyOTP(identifier)
       return Triple(status, if (status == "pending") "Votre code de vérification a été envoyé avec suucès" else "Erreur identifiant non prises en charge",identifier)
    }
    suspend fun verifyOTP(user : VerifyRequest): Pair<Long, String?> {
       val userSecurity = userRepository.findByPhoneOrEmail(user.identifier) ?: throw ResponseStatusException(HttpStatusCode.valueOf(403), "Idenfiant invalide.")
       return Pair(userSecurity.userId!!,twilio.checkVerify(code = user.code, contact = user.identifier))
    }
    suspend fun changePassword(id : Long,new : String): UserDto {
        val data = userRepository.findById(id)
        if (data != null) {
            data.password = hashEncoder.encode(new)
            val updatedUser = userRepository.save(data)
            return updatedUser.toDomain()
        }
        throw ResponseStatusException(HttpStatusCode.valueOf(403), "ID invalide.")
    }
    suspend fun goCertification(id : Long,state : Boolean) = coroutineScope {
        val user = userRepository.findById(id)?:throw ResponseStatusException(
            HttpStatusCode.valueOf(404),
            "ID Is Not Found for User with ID $id."
        )
        user.isCertified = state
        userRepository.save(user)
    }
    suspend fun lockedOrUnlocked(userId: Long, isLock: Boolean = true) : Boolean = coroutineScope{
        log.info("user method -> $userId")
    val state = when {
            userRepository.findById(userId) != null -> {
                log.info("in")
//                if (prestation.findByUser(userId).toList().isNotEmpty())
//                    prestation.setUpdateIsAvailable(userId, !isLock)
//                if (bureau.findAllByUser(userId).toList().isNotEmpty())
//                    bureau.setUpdateIsAvailable(userId, !isLock)
//                if (property.findAllByUser(userId).toList().isNotEmpty())
//                    property.setUpdateIsAvailable(userId, !isLock)
//                if (funeraire.findAllByUser(userId).toList().isNotEmpty())
//                    funeraire.setUpdateIsAvailable(userId, !isLock)
//                if (festive.findAllByUser(userId).toList().isNotEmpty())
//                    festive.setUpdateIsAvailable(userId, !isLock)
//                if (terrain.findAllByUser(userId).toList().isNotEmpty())
//                    terrain.setUpdateIsAvailable(userId, !isLock)
//                if (hotel.getAllByUser(userId).toList().isNotEmpty())
//                    hotel.setUpdateIsAvailable(userId, !isLock)
//                if (person.findByUser(userId) != null)
//                    person.isLock(userId, isLock)
                userRepository.isLock(userId, isLock)
                true
            }
            else-> false
        }
        state
    }

    @Transactional
    suspend fun refresh(refreshToken: String): TokenPair {
        if(!jwtService.validateRefreshToken(refreshToken)) throw ResponseStatusException(HttpStatusCode.valueOf(403), "Invalid refresh token.")
        val userId = jwtService.getUserIdFromToken(refreshToken)
        val user = userRepository.findById(userId.toLong()) ?: throw ResponseStatusException(HttpStatusCode.valueOf(403), "Invalid refresh token.")
        val hashed = hashToken(refreshToken)
        refreshTokenRepository.findByUserIdAndHashedToken(user.userId!!, hashed) ?: throw ResponseStatusException(HttpStatusCode.valueOf(403), "Refresh token not recognized (maybe used or expired?)")
        refreshTokenRepository.deleteByUserIdAndHashedToken(user.userId, hashed)
        val newAccessToken = jwtService.generateAccessToken(userId)
        val newRefreshToken = jwtService.generateRefreshToken(userId)
//        storeRefreshToken(user.userId, newRefreshToken)
        return TokenPair(accessToken = newAccessToken, refreshToken = newRefreshToken)
    }
//    @OptIn(ExperimentalTime::class)
//    private suspend fun storeRefreshToken(userId: Long, rawRefreshToken: String) {
//        val hashed = hashToken(rawRefreshToken)
//        val expiryMs = jwtService.refreshTokenValidityMs
//        val instant = Clock.System.now().plusMillis(expiryMs)
//        val zoneId = ZoneId.systemDefault()
//        val expiresAt =  LocalDateTime.ofInstant(instant, zoneId)
//        refreshTokenRepository.save(RefreshToken(userId = userId, expiresAt = expiresAt, hashedToken = hashed))
//    }
    private fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}