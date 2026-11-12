package t3digitalgroup.vehnixauto.server.app.delivery.infrastructure.controllers

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
import t3digitalgroup.vehnixauto.server.app.delivery.application.services.PartDeliveryService
import t3digitalgroup.vehnixauto.server.app.delivery.domain.models.PartDeliveryAssignRequest
import t3digitalgroup.vehnixauto.server.app.delivery.domain.models.PartDeliveryCreate
import t3digitalgroup.vehnixauto.server.app.delivery.domain.models.PartDeliveryTrackingRequest
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.delivery.DeliveryScope
import t3digitalgroup.vehnixauto.server.security.AdminAuthorization
import t3digitalgroup.vehnixauto.server.security.Auth
import t3digitalgroup.vehnixauto.server.security.monitoring.MetricModel
import t3digitalgroup.vehnixauto.server.security.monitoring.SentryService
import t3digitalgroup.vehnixauto.server.utils.DeliveryStatus

@Tag(name = "Part Delivery", description = "Livraison des pièces de rechange")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class PartDeliveryController(
    private val service: PartDeliveryService,
    private val auth: Auth,
    private val adminAuthorization: AdminAuthorization,
    private val sentry: SentryService,
) {
    @Operation(summary = "Créer une demande de livraison de pièce")
    @PostMapping(DeliveryScope.PROTECTED, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun create(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: PartDeliveryCreate,
    ) = tracked(request, "api.delivery.create") {
        val userId = auth.user()?.first?.userId
            ?: return@tracked ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("message" to "Auth requise"))
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(userId, body))
    }

    @Operation(summary = "Mes livraisons")
    @GetMapping("${DeliveryScope.PROTECTED}/mine", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun mine(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = tracked(request, "api.delivery.mine") {
        val userId = auth.user()?.first?.userId
            ?: return@tracked ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("message" to "Auth requise"))
        ResponseEntity.ok(service.findMine(userId))
    }

    @Operation(summary = "Détail livraison")
    @GetMapping("${DeliveryScope.PROTECTED}/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findById(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = tracked(request, "api.delivery.find") {
        ResponseEntity.ok(service.findById(id))
    }

    @Operation(summary = "Lister livraisons (admin)")
    @GetMapping("${DeliveryScope.PROTECTED}/admin/all", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun adminAll(
        request: HttpServletRequest,
        @PathVariable version: String,
        @RequestParam(required = false) status: DeliveryStatus?,
    ) = tracked(request, "api.delivery.admin") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.findAll(status))
    }

    @Operation(summary = "Assigner un livreur")
    @PatchMapping("${DeliveryScope.PROTECTED}/admin/{id}/assign", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun assign(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
        @Valid @RequestBody body: PartDeliveryAssignRequest,
    ) = tracked(request, "api.delivery.assign") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.assignCourier(id, body.courierUserId))
    }

    @Operation(summary = "Mettre à jour le statut de livraison")
    @PatchMapping("${DeliveryScope.PROTECTED}/admin/{id}/status", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun status(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
        @RequestParam status: DeliveryStatus,
    ) = tracked(request, "api.delivery.status") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.updateStatus(id, status))
    }

    @Operation(summary = "Publier une position de livraison (Depart / En route…)")
    @PostMapping("${DeliveryScope.PROTECTED}/{id}/tracking", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun track(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
        @Valid @RequestBody body: PartDeliveryTrackingRequest,
    ) = tracked(request, "api.delivery.track") {
        val userId = auth.user()?.first?.userId
        ResponseEntity.status(HttpStatus.CREATED).body(service.track(id, userId, body))
    }

    @Operation(summary = "Historique tracking livraison")
    @GetMapping("${DeliveryScope.PROTECTED}/{id}/tracking", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun trackingHistory(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = tracked(request, "api.delivery.tracking.history") {
        ResponseEntity.ok(service.trackingHistory(id))
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
