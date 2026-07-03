package t3digitalgroup.vehnixauto.server.app.user.application.services

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.slf4j.*
import org.springframework.context.annotation.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*
import org.springframework.web.server.*
import t3digitalgroup.vehnixauto.server.adaptater.provider.twilio.TwilioService
import t3digitalgroup.vehnixauto.server.app.user.domain.models.User
import t3digitalgroup.vehnixauto.server.app.user.domain.models.UserDto
import t3digitalgroup.vehnixauto.server.app.user.domain.models.UserFullDTO
import t3digitalgroup.vehnixauto.server.app.user.domain.models.request.VerifyRequest
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.entities.AccountDTO
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.entities.AccountUserEntity
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.entities.RefreshToken
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.mapper.toDomain
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.mapper.toEntity
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.repositories.AccountUserRepository
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.repositories.RefreshTokenRepository
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.repositories.UserRepository
import t3digitalgroup.vehnixauto.server.security.HashEncoder
import t3digitalgroup.vehnixauto.server.security.JwtService
import t3digitalgroup.vehnixauto.server.utils.Mode
import t3digitalgroup.vehnixauto.server.utils.isEmailValid
import t3digitalgroup.vehnixauto.server.utils.normalizeAndValidatePhoneNumberUniversal
import java.security.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.time.ExperimentalTime

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
    private val accountUserRepository: AccountUserRepository,
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    data class TokenPair(
        val accessToken: String,
        val refreshToken: String
    )
    suspend fun register(user: User): UserDto? = coroutineScope {
        var state = false
        if (user.email != null){
            if(user.email.isNotEmpty()){
                if(userRepository.findByPhoneOrEmail(user.email) != null) throw ResponseStatusException(HttpStatus.CONFLICT, "Cette adresse mail est déjà associé à un compte existant.")
                state = true
            }
        }
        if (!state) throw ResponseStatusException(HttpStatus.CONFLICT, "Vous devez renseigner l'email")
        val entity = user.toEntity().apply {
            password = hashEncoder.encode(user.password)
        }
        log.info("Creating user ${user.userId}")
        val savedEntity = userRepository.save(entity)
        val userData : UserDto = savedEntity.toDomain()
        log.info("Creating user after -> ${userData.userId}")
        accountUserRepository.save(AccountUserEntity(null, 1, userData.userId ?: 0))
        userData
    }
    suspend fun login(identifier: String, password: String): Pair<TokenPair, UserFullDTO>  =
        coroutineScope {
            log.info("Logging into")
            var validIdentifier = normalizeAndValidatePhoneNumberUniversal(identifier)
            if (isEmailValid(identifier)) validIdentifier = identifier
            val user = userRepository.findByPhoneOrEmail(validIdentifier.toString()) ?: throw ResponseStatusException(HttpStatusCode.valueOf(403), "Invalid credentials.")
            if(!hashEncoder.matches(password, user.password.toString())) throw ResponseStatusException(HttpStatusCode.valueOf(403), "Invalid credentials.")
            log.info("Logging into user ${user.userId}")
            val newAccessToken = jwtService.generateAccessToken(user.userId!!.toHexString())
            val newRefreshToken = jwtService.generateRefreshToken(user.userId.toHexString())
            log.info("after generate ${user.userId}")
            val accounts = serviceMultiAccount.getAll().filter { it.userId == user.userId }.toList()
            val accountMultiple: List<AccountDTO> =  accounts.map {
                val data = accountService.findByIdAccount(it.accountId)
                AccountDTO(
                    id = data.id,
                    name = data.name,
                    typeAccount = typeAccountService.findByIdTypeAccount(data.typeAccountId)
                )
            }.toList()
            val profile = userRepository.findById(user.userId)
            storeRefreshToken(user.userId, newRefreshToken)
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
    @OptIn(ExperimentalTime::class)
    private suspend fun storeRefreshToken(userId: Long, rawRefreshToken: String) {
        val hashed = hashToken(rawRefreshToken)
        val expiryMs = jwtService.refreshTokenValidityMs
        val instant = Instant.now().plusMillis(expiryMs)
        val zoneId = ZoneId.systemDefault()
        val expiresAt =  LocalDateTime.ofInstant(instant, zoneId)
        refreshTokenRepository.save(RefreshToken(userId = userId, expiresAt = expiresAt, hashedToken = hashed))
    }
    private fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}