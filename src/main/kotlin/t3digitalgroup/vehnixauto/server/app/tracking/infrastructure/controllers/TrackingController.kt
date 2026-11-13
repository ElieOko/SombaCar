package t3digitalgroup.vehnixauto.server.app.tracking.infrastructure.controllers

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
import t3digitalgroup.vehnixauto.server.app.tracking.application.services.TrackingService
import t3digitalgroup.vehnixauto.server.app.tracking.domain.models.TrackingCarDTO
import t3digitalgroup.vehnixauto.server.app.tracking.domain.models.TrackingEventRequest
import t3digitalgroup.vehnixauto.server.app.tracking.domain.models.TrackingMotoDTO
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.tracking.TrackingScope
import t3digitalgroup.vehnixauto.server.security.AdminAuthorization
import t3digitalgroup.vehnixauto.server.security.Auth
import t3digitalgroup.vehnixauto.server.security.monitoring.MetricModel
import t3digitalgroup.vehnixauto.server.security.monitoring.SentryService
import t3digitalgroup.vehnixauto.server.utils.VehicleKind

@Tag(name = "Tracking", description = "Géolocalisation temps réel voitures et motos")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class TrackingController(
    private val service: TrackingService,
    private val auth: Auth,
    private val adminAuthorization: AdminAuthorization,
    private val sentry: SentryService,
) {
    @Operation(summary = "Publier une position tracking (générique)")
    @PostMapping("${TrackingScope.PROTECTED}/events", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun publishEvent(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: TrackingEventRequest,
    ) = tracked(request, "api.tracking.publish") {
        val userId = auth.user()?.first?.userId
        ResponseEntity.status(HttpStatus.CREATED).body(service.publish(userId, body))
    }

    @Operation(summary = "Publier tracking voiture (tag Depart puis En route…)")
    @PostMapping("${TrackingScope.PROTECTED}/cars", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun publishCar(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: TrackingCarDTO,
    ) = tracked(request, "api.tracking.car") {
        val userId = auth.user()?.first?.userId
        ResponseEntity.status(HttpStatus.CREATED).body(service.publishCar(userId, body))
    }

    @Operation(summary = "Publier tracking moto")
    @PostMapping("${TrackingScope.PROTECTED}/motos", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun publishMoto(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: TrackingMotoDTO,
    ) = tracked(request, "api.tracking.moto") {
        val userId = auth.user()?.first?.userId
        ResponseEntity.status(HttpStatus.CREATED).body(service.publishMoto(userId, body))
    }

    @Operation(summary = "Historique tracking d'un véhicule (admin)")
    @GetMapping("${TrackingScope.PROTECTED}/admin/{vehicleType}/{vehicleId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun adminHistory(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable vehicleType: VehicleKind,
        @PathVariable vehicleId: Long,
    ) = tracked(request, "api.tracking.admin.history") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.history(vehicleType, vehicleId))
    }

    @Operation(summary = "Dernière position active (admin)")
    @GetMapping("${TrackingScope.PROTECTED}/admin/{vehicleType}/{vehicleId}/latest", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun adminLatest(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable vehicleType: VehicleKind,
        @PathVariable vehicleId: Long,
    ) = tracked(request, "api.tracking.admin.latest") {
        adminAuthorization.requireAdmin()
        ResponseEntity.ok(service.latest(vehicleType, vehicleId))
    }

    @Operation(summary = "Historique tracking lié à une demande de location")
    @GetMapping("${TrackingScope.PROTECTED}/rental/{rentalRequestId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun byRental(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable rentalRequestId: Long,
    ) = tracked(request, "api.tracking.rental") {
        ResponseEntity.ok(service.historyByRental(rentalRequestId))
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
