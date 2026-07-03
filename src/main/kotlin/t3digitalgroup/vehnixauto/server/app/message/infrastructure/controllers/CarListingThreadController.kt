package t3digitalgroup.vehnixauto.server.app.message.infrastructure.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import t3digitalgroup.vehnixauto.server.app.message.application.services.CarListingThreadService
import t3digitalgroup.vehnixauto.server.app.message.domain.models.CarListingThreadStatus
import t3digitalgroup.vehnixauto.server.app.message.domain.models.request.CarListingThreadRequest
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.message.CarListingThreadScope
import t3digitalgroup.vehnixauto.server.security.monitoring.MetricModel
import t3digitalgroup.vehnixauto.server.security.monitoring.SentryService
import t3digitalgroup.vehnixauto.server.utils.ApiResponse

@Tag(name = "Car Listing Thread", description = "Conversations liées aux annonces de voitures")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class CarListingThreadController(
    private val service: CarListingThreadService,
    private val sentry: SentryService,
) {
    @Operation(summary = "Contacter le propriétaire d'une annonce")
    @PostMapping(CarListingThreadScope.PROTECTED, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun openThread(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: CarListingThreadRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ResponseEntity.status(HttpStatus.CREATED).body(service.openThread(body))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.carlistingthread.openthread.count",
                    distributionName = "api.carlistingthread.openthread.latency",
                )
            )
        }
    }

    @Operation(summary = "Détail d'une conversation")
    @GetMapping("${CarListingThreadScope.PROTECTED}/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findById(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ResponseEntity.ok(service.findById(id))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.carlistingthread.findbyid.count",
                    distributionName = "api.carlistingthread.findbyid.latency",
                )
            )
        }
    }

    @Operation(summary = "Conversations d'un utilisateur (acheteur ou vendeur)")
    @GetMapping("${CarListingThreadScope.PROTECTED}/user/{userId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findByUserId(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable userId: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.findByParticipantUserId(userId).toList())
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.carlistingthread.findbyuserid.count",
                    distributionName = "api.carlistingthread.findbyuserid.latency",
                )
            )
        }
    }

    @Operation(summary = "Conversations liées à une annonce")
    @GetMapping("${CarListingThreadScope.PROTECTED}/listing/{carListingId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findByCarListingId(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable carListingId: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.findByCarListingId(carListingId).toList())
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.carlistingthread.findbycarlistingid.count",
                    distributionName = "api.carlistingthread.findbycarlistingid.latency",
                )
            )
        }
    }

    @Operation(summary = "Clôturer une conversation")
    @PatchMapping("${CarListingThreadScope.PROTECTED}/{id}/status", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun updateStatus(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
        @RequestParam status: CarListingThreadStatus,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ResponseEntity.ok(service.updateStatus(id, status))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.carlistingthread.updatestatus.count",
                    distributionName = "api.carlistingthread.updatestatus.latency",
                )
            )
        }
    }
}
