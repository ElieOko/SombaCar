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
