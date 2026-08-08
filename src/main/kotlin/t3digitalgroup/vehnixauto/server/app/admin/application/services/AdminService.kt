package t3digitalgroup.vehnixauto.server.app.admin.application.services

import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.car.application.services.CarListingService
import t3digitalgroup.vehnixauto.server.app.moto.application.services.MotoListingService
import t3digitalgroup.vehnixauto.server.app.tools.application.services.PartListingService
import t3digitalgroup.vehnixauto.server.app.user.application.services.AccountService
import t3digitalgroup.vehnixauto.server.app.user.application.services.AccountUserService
import t3digitalgroup.vehnixauto.server.app.user.application.services.TypeAccountService
import t3digitalgroup.vehnixauto.server.app.user.application.services.UserService
import t3digitalgroup.vehnixauto.server.app.user.domain.models.Account
import t3digitalgroup.vehnixauto.server.app.user.domain.models.AccountUser
import t3digitalgroup.vehnixauto.server.app.user.domain.models.TypeAccount
import t3digitalgroup.vehnixauto.server.app.user.domain.models.UserDto
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.repositories.AccountUserRepository
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.repositories.UserRepository
import t3digitalgroup.vehnixauto.server.utils.ListingStatus
import t3digitalgroup.vehnixauto.server.utils.Mode

@Service
@Profile(Mode.DEV)
class AdminService(
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val accountUserRepository: AccountUserRepository,
    private val accountUserService: AccountUserService,
    private val accountService: AccountService,
    private val typeAccountService: TypeAccountService,
    private val carListingService: CarListingService,
    private val motoListingService: MotoListingService,
    private val partListingService: PartListingService,
) {
    suspend fun changeUserRole(userId: Long, accountId: Long): UserDto {
        userService.findIdUser(userId)
        accountService.findByIdAccount(accountId)
        accountUserRepository.findAllAccountByUserId(userId).toList().forEach {
            accountUserRepository.deleteById(it.id!!)
        }
        accountUserService.save(AccountUser(userId = userId, accountId = accountId))
        return userService.findIdUser(userId)
    }

    suspend fun setUserLock(userId: Long, locked: Boolean): UserDto {
        val user = userRepository.findById(userId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable.")
        user.isLock = locked
        userRepository.save(user)
        return userService.findIdUser(userId)
    }

    suspend fun deactivateCarListing(listingId: Long) =
        carListingService.updateStatus(listingId, ListingStatus.INACTIVE)

    suspend fun deactivateMotoListing(listingId: Long) =
        motoListingService.updateStatus(listingId, ListingStatus.INACTIVE)

    suspend fun deactivatePartListing(listingId: Long) =
        partListingService.updateStatus(listingId, ListingStatus.INACTIVE)

    suspend fun createTypeAccount(typeAccount: TypeAccount): TypeAccount =
        typeAccountService.saveAccount(typeAccount)

    suspend fun createAccount(account: Account): Account =
        accountService.save(account)
}
