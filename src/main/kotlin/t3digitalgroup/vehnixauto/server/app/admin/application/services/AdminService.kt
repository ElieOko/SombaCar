package t3digitalgroup.vehnixauto.server.app.admin.application.services

import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.car.application.services.CarListingService
import t3digitalgroup.vehnixauto.server.app.moto.application.services.MotoListingService
import t3digitalgroup.vehnixauto.server.app.report.application.services.ListingReportService
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
import t3digitalgroup.vehnixauto.server.utils.OfferType

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
    private val listingReportService: ListingReportService,
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

    suspend fun banCarListing(listingId: Long) =
        carListingService.updateStatus(listingId, ListingStatus.BANNED)

    suspend fun banMotoListing(listingId: Long) =
        motoListingService.updateStatus(listingId, ListingStatus.BANNED)

    suspend fun banPartListing(listingId: Long) =
        partListingService.updateStatus(listingId, ListingStatus.BANNED)

    suspend fun findCarListingById(listingId: Long) =
        adminListingDetail(OfferType.CAR, listingId, carListingService.findById(listingId, includeDocuments = true))

    suspend fun findMotoListingById(listingId: Long) =
        adminListingDetail(OfferType.MOTO, listingId, motoListingService.findById(listingId, includeDocuments = true))

    suspend fun findPartListingById(listingId: Long) =
        adminListingDetail(OfferType.PART, listingId, partListingService.findById(listingId))

    private suspend fun adminListingDetail(listingType: OfferType, listingId: Long, listing: Any) =
        mapOf(
            "listing" to listing,
            "reportCount" to listingReportService.countByListing(listingType, listingId),
            "deactivationThreshold" to ListingReportService.DEACTIVATION_THRESHOLD,
        )

    suspend fun findAllCarListings(status: ListingStatus? = null) =
        carListingService.findAllForAdmin(status)

    suspend fun findAllMotoListings(status: ListingStatus? = null) =
        motoListingService.findAllForAdmin(status)

    suspend fun findAllPartListings(status: ListingStatus? = null) =
        partListingService.findAllForAdmin(status)

    suspend fun activateCarListing(listingId: Long) =
        carListingService.updateStatus(listingId, ListingStatus.ACTIVE)

    suspend fun activateMotoListing(listingId: Long) =
        motoListingService.updateStatus(listingId, ListingStatus.ACTIVE)

    suspend fun activatePartListing(listingId: Long) =
        partListingService.updateStatus(listingId, ListingStatus.ACTIVE)

    suspend fun unbanCarListing(listingId: Long) =
        reactivateListing(OfferType.CAR, listingId)

    suspend fun unbanMotoListing(listingId: Long) =
        reactivateListing(OfferType.MOTO, listingId)

    suspend fun unbanPartListing(listingId: Long) =
        reactivateListing(OfferType.PART, listingId)

    suspend fun findAllTypeAccounts() =
        typeAccountService.getAll()

    suspend fun createTypeAccount(typeAccount: TypeAccount): TypeAccount =
        typeAccountService.saveAccount(typeAccount)

    suspend fun createAccount(account: Account): Account =
        accountService.save(account)

    private suspend fun reactivateListing(listingType: OfferType, listingId: Long) = when (listingType) {
        OfferType.CAR -> activateCarListing(listingId)
        OfferType.MOTO -> activateMotoListing(listingId)
        OfferType.PART -> activatePartListing(listingId)
    }
}
