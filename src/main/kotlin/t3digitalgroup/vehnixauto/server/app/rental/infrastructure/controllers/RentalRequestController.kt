package t3digitalgroup.vehnixauto.server.app.rental.infrastructure.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import kotlinx.coroutines.coroutineScope
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import t3digitalgroup.vehnixauto.server.app.rental.application.services.RentalRequestService
import t3digitalgroup.vehnixauto.server.app.rental.domain.models.RentalQuoteRequest
import t3digitalgroup.vehnixauto.server.app.rental.domain.models.RentalRequestCreate
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.rental.RentalScope
import t3digitalgroup.vehnixauto.server.security.AdminAuthorization
import t3digitalgroup.vehnixauto.server.security.Auth
import t3digitalgroup.vehnixauto.server.security.monitoring.MetricModel
import t3digitalgroup.vehnixauto.server.security.monitoring.SentryService
import t3digitalgroup.vehnixauto.server.utils.RentalRequestStatus

@Tag(name = "Rental", description = "Demandes de location avec chauffeur et paiement via chat")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class RentalRequestController(
    private val service: RentalRequestService,
    private val auth: Auth,
    private val adminAuthorization: AdminAuthorization,
    private val sentry: SentryService,
) {
    @Operation(summary = "Soumettre une demande de location")
    @PostMapping(RentalScope.PROTECTED, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun create(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: RentalRequestCreate,
    ) = tracked(request, "api.rental.create") {
        val userId = auth.user()?.first?.userId
            ?: return@tracked ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("message" to "Auth requise"))
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(userId, body))
    }

    @Operation(summary = "Mes demandes de location")
    @GetMapping("${RentalScope.PROTECTED}/mine", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun mine(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = tracked(request, "api.rental.mine") {
        val userId = auth.user()?.first?.userId
            ?: return@tracked ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("message" to "Auth requise"))
        ResponseEntity.ok(service.findMine(userId))
    }

    @Operation(summary = "Détail d'une demande de location")
    @GetMapping("${RentalScope.PROTECTED}/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findById(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = tracked(request, "api.rental.find") {
        ResponseEntity.ok(service.findById(id))
    }

    @Operation(summary = "Lister les demandes (admin)")
    @GetMapping("${RentalScope.PROTECTED}/admin/all", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun adminAll(
        request: HttpServletRequest,
        @PathVariable version: String,
        @RequestParam(required = false) status: RentalRequestStatus?,
    ) = tracked(request, "api.rental.admin.all") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.findAll(status))
    }

    @Operation(summary = "Envoyer devis / lien de paiement via chat (admin)")
    @PutMapping("${RentalScope.PROTECTED}/admin/{id}/quote", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun quote(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
        @Valid @RequestBody body: RentalQuoteRequest,
    ) = tracked(request, "api.rental.quote") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.updateQuote(id, body))
    }

    @Operation(summary = "Mettre à jour le statut de réservation")
    @PatchMapping("${RentalScope.PROTECTED}/admin/{id}/status", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun status(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
        @RequestParam status: RentalRequestStatus,
    ) = tracked(request, "api.rental.status") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.updateStatus(id, status))
    }

    private suspend fun tracked(
        request: HttpServletRequest,
        metric: String,
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
                    countName = "$metric.count",
                    distributionName = "$metric.latency",
                )
            )
        }
    }
}
