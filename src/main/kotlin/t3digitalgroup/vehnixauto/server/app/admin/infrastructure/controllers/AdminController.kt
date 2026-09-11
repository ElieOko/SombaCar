package t3digitalgroup.vehnixauto.server.app.admin.infrastructure.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import kotlinx.coroutines.coroutineScope
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import t3digitalgroup.vehnixauto.server.app.admin.application.services.AdminService
import t3digitalgroup.vehnixauto.server.app.admin.domain.models.request.ChangeUserRoleRequest
import t3digitalgroup.vehnixauto.server.app.admin.domain.models.request.UserLockRequest
import t3digitalgroup.vehnixauto.server.app.user.domain.models.Account
import t3digitalgroup.vehnixauto.server.app.user.domain.models.TypeAccount
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.admin.AdminScope
import t3digitalgroup.vehnixauto.server.security.AdminAuthorization
import t3digitalgroup.vehnixauto.server.security.monitoring.MetricModel
import t3digitalgroup.vehnixauto.server.security.monitoring.SentryService
import t3digitalgroup.vehnixauto.server.utils.ListingStatus

@Tag(name = "Admin", description = "Administration de la plateforme")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class AdminController(
    private val service: AdminService,
    private val adminAuthorization: AdminAuthorization,
    private val sentry: SentryService,
) {
    @Operation(summary = "Changer le rôle d'un utilisateur")
    @PutMapping("${AdminScope.PROTECTED}/users/{userId}/role", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun changeUserRole(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable userId: Long,
        @Valid @RequestBody body: ChangeUserRoleRequest,
    ) = adminAction(request, "api.admin.changeuserrole") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.changeUserRole(userId, body.accountId))
    }

    @Operation(summary = "Activer ou désactiver un compte utilisateur")
    @PutMapping("${AdminScope.PROTECTED}/users/{userId}/lock", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun setUserLock(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable userId: Long,
        @Valid @RequestBody body: UserLockRequest,
    ) = adminAction(request, "api.admin.setuserlock") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.setUserLock(userId, body.locked))
    }

    @Operation(summary = "Désactiver une annonce voiture")
    @PatchMapping("${AdminScope.PROTECTED}/cars/listings/{id}/deactivate", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun deactivateCarListing(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = adminAction(request, "api.admin.deactivatecar") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.deactivateCarListing(id))
    }

    @Operation(summary = "Désactiver une annonce moto")
    @PatchMapping("${AdminScope.PROTECTED}/motos/listings/{id}/deactivate", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun deactivateMotoListing(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = adminAction(request, "api.admin.deactivatemoto") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.deactivateMotoListing(id))
    }

    @Operation(summary = "Désactiver une annonce pièce")
    @PatchMapping("${AdminScope.PROTECTED}/parts/listings/{id}/deactivate", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun deactivatePartListing(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = adminAction(request, "api.admin.deactivatepart") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.deactivatePartListing(id))
    }

    @Operation(summary = "Bannir une annonce voiture")
    @PatchMapping("${AdminScope.PROTECTED}/cars/listings/{id}/ban", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun banCarListing(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = adminAction(request, "api.admin.bancar") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.banCarListing(id))
    }

    @Operation(summary = "Bannir une annonce moto")
    @PatchMapping("${AdminScope.PROTECTED}/motos/listings/{id}/ban", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun banMotoListing(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = adminAction(request, "api.admin.banmoto") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.banMotoListing(id))
    }

    @Operation(summary = "Bannir une annonce pièce")
    @PatchMapping("${AdminScope.PROTECTED}/parts/listings/{id}/ban", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun banPartListing(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = adminAction(request, "api.admin.banpart") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.banPartListing(id))
    }

    @Operation(summary = "Détail d'une annonce voiture (admin, inclut désactivées et bannies)")
    @GetMapping("${AdminScope.PROTECTED}/cars/listings/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findCarListingById(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = adminAction(request, "api.admin.findcarbyid") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.findCarListingById(id))
    }

    @Operation(summary = "Détail d'une annonce moto (admin, inclut désactivées et bannies)")
    @GetMapping("${AdminScope.PROTECTED}/motos/listings/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findMotoListingById(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = adminAction(request, "api.admin.findmotobyid") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.findMotoListingById(id))
    }

    @Operation(summary = "Détail d'une annonce pièce (admin, inclut désactivées et bannies)")
    @GetMapping("${AdminScope.PROTECTED}/parts/listings/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findPartListingById(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = adminAction(request, "api.admin.findpartbyid") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.findPartListingById(id))
    }

    @Operation(summary = "Lister toutes les annonces voiture (admin, inclut les désactivées)")
    @GetMapping("${AdminScope.PROTECTED}/cars/listings", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findAllCarListings(
        request: HttpServletRequest,
        @PathVariable version: String,
        @RequestParam(required = false) status: ListingStatus?,
    ) = adminAction(request, "api.admin.findallcar") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.findAllCarListings(status))
    }

    @Operation(summary = "Lister toutes les annonces moto (admin, inclut les désactivées)")
    @GetMapping("${AdminScope.PROTECTED}/motos/listings", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findAllMotoListings(
        request: HttpServletRequest,
        @PathVariable version: String,
        @RequestParam(required = false) status: ListingStatus?,
    ) = adminAction(request, "api.admin.findallmoto") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.findAllMotoListings(status))
    }

    @Operation(summary = "Lister toutes les annonces pièce (admin, inclut les désactivées)")
    @GetMapping("${AdminScope.PROTECTED}/parts/listings", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findAllPartListings(
        request: HttpServletRequest,
        @PathVariable version: String,
        @RequestParam(required = false) status: ListingStatus?,
    ) = adminAction(request, "api.admin.findallpart") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.findAllPartListings(status))
    }

    @Operation(summary = "Réactiver une annonce voiture")
    @PatchMapping("${AdminScope.PROTECTED}/cars/listings/{id}/activate", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun activateCarListing(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = adminAction(request, "api.admin.activatecar") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.activateCarListing(id))
    }

    @Operation(summary = "Réactiver une annonce moto")
    @PatchMapping("${AdminScope.PROTECTED}/motos/listings/{id}/activate", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun activateMotoListing(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = adminAction(request, "api.admin.activatemoto") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.activateMotoListing(id))
    }

    @Operation(summary = "Réactiver une annonce pièce")
    @PatchMapping("${AdminScope.PROTECTED}/parts/listings/{id}/activate", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun activatePartListing(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = adminAction(request, "api.admin.activatepart") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.activatePartListing(id))
    }

    @Operation(summary = "Lever le bannissement d'une annonce voiture")
    @PatchMapping("${AdminScope.PROTECTED}/cars/listings/{id}/unban", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun unbanCarListing(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = adminAction(request, "api.admin.unbancar") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.unbanCarListing(id))
    }

    @Operation(summary = "Lever le bannissement d'une annonce moto")
    @PatchMapping("${AdminScope.PROTECTED}/motos/listings/{id}/unban", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun unbanMotoListing(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = adminAction(request, "api.admin.unbanmoto") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.unbanMotoListing(id))
    }

    @Operation(summary = "Lever le bannissement d'une annonce pièce")
    @PatchMapping("${AdminScope.PROTECTED}/parts/listings/{id}/unban", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun unbanPartListing(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = adminAction(request, "api.admin.unbanpart") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.unbanPartListing(id))
    }

    @Operation(summary = "Lister tous les types de comptes (admin)")
    @GetMapping("${AdminScope.PROTECTED}/accounts/type", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findAllTypeAccounts(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = adminAction(request, "api.admin.findalltypeaccounts") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.findAllTypeAccounts())
    }

    @Operation(summary = "Créer un type de compte")
    @PostMapping("${AdminScope.PROTECTED}/accounts/type", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun createTypeAccount(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: TypeAccount,
    ) = adminAction(request, "api.admin.createtypeaccount") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.createTypeAccount(body))
    }

    @Operation(summary = "Créer un compte (rôle)")
    @PostMapping("${AdminScope.PROTECTED}/accounts", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun createAccount(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: Account,
    ) = adminAction(request, "api.admin.createaccount") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.createAccount(body))
    }

    private suspend fun adminAction(
        request: HttpServletRequest,
        metricName: String,
        block: suspend () -> ResponseEntity<*>,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            block()
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "$metricName.count",
                    distributionName = "$metricName.latency",
                )
            )
        }
    }
}
