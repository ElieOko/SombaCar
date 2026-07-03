package t3digitalgroup.vehnixauto.server.app.message.infrastructure.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import t3digitalgroup.vehnixauto.server.app.message.application.services.SupportThreadService
import t3digitalgroup.vehnixauto.server.app.message.domain.models.SupportThreadStatus
import t3digitalgroup.vehnixauto.server.app.message.domain.models.request.SupportThreadRequest
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.message.SupportThreadScope
import t3digitalgroup.vehnixauto.server.security.monitoring.MetricModel
import t3digitalgroup.vehnixauto.server.security.monitoring.SentryService
import t3digitalgroup.vehnixauto.server.utils.ApiResponse

@Tag(name = "Support Thread", description = "Gestion des demandes de pièces détachées")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class SupportThreadController(
    private val service: SupportThreadService,
    private val sentry: SentryService,
) {
    @Operation(summary = "Ouvrir une demande de support")
    @PostMapping(SupportThreadScope.PROTECTED, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun openThread(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: SupportThreadRequest,
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
                    countName = "api.supportthread.openthread.count",
                    distributionName = "api.supportthread.openthread.latency",
                )
            )
        }
    }

    @Operation(summary = "Détail d'une demande de support")
    @GetMapping("${SupportThreadScope.PROTECTED}/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
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
                    countName = "api.supportthread.findbyid.count",
                    distributionName = "api.supportthread.findbyid.latency",
                )
            )
        }
    }

    @Operation(summary = "Demandes de support d'un utilisateur")
    @GetMapping("${SupportThreadScope.PROTECTED}/user/{userId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findByUserId(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable userId: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.findByUserId(userId).toList())
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.supportthread.findbyuserid.count",
                    distributionName = "api.supportthread.findbyuserid.latency",
                )
            )
        }
    }

    @Operation(summary = "Liste des demandes ouvertes")
    @GetMapping("${SupportThreadScope.PROTECTED}/open", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findOpenThreads(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.findOpenThreads().toList())
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.supportthread.findopenthreads.count",
                    distributionName = "api.supportthread.findopenthreads.latency",
                )
            )
        }
    }

    @Operation(summary = "Mettre à jour le statut d'une demande")
    @PatchMapping("${SupportThreadScope.PROTECTED}/{id}/status", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun updateStatus(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
        @RequestParam status: SupportThreadStatus,
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
                    countName = "api.supportthread.updatestatus.count",
                    distributionName = "api.supportthread.updatestatus.latency",
                )
            )
        }
    }
}
