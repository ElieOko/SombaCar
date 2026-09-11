package t3digitalgroup.vehnixauto.server.app.report.infrastructure.controllers

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
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.report.application.services.ListingReportService
import t3digitalgroup.vehnixauto.server.app.report.domain.models.request.ListingReportRequest
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.report.ListingReportScope
import t3digitalgroup.vehnixauto.server.security.AdminAuthorization
import t3digitalgroup.vehnixauto.server.security.Auth
import t3digitalgroup.vehnixauto.server.security.monitoring.MetricModel
import t3digitalgroup.vehnixauto.server.security.monitoring.SentryService
import t3digitalgroup.vehnixauto.server.utils.ApiResponse
import t3digitalgroup.vehnixauto.server.utils.OfferType

@Tag(name = "Listing Report", description = "Signalement des annonces")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class ListingReportController(
    private val service: ListingReportService,
    private val auth: Auth,
    private val adminAuthorization: AdminAuthorization,
    private val sentry: SentryService,
) {
    @Operation(summary = "Signaler une annonce")
    @PostMapping(ListingReportScope.PROTECTED, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun report(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: ListingReportRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            ResponseEntity.status(HttpStatus.CREATED).body(service.report(userId, body))
        } finally {
            recordMetric(request, startNanos, "api.listingreport.create")
        }
    }

    @Operation(summary = "Mes signalements")
    @GetMapping("${ListingReportScope.PROTECTED}/mine", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findMine(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            ApiResponse(service.findByUser(userId))
        } finally {
            recordMetric(request, startNanos, "api.listingreport.mine")
        }
    }

    @Operation(summary = "Nombre de signalements d'une annonce")
    @GetMapping("${ListingReportScope.PROTECTED}/{listingType}/{listingId}/count", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun countByListing(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable listingType: OfferType,
        @PathVariable listingId: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ResponseEntity.ok(
                mapOf(
                    "listingType" to listingType.name,
                    "listingId" to listingId,
                    "reportCount" to service.countByListing(listingType, listingId),
                    "deactivationThreshold" to ListingReportService.DEACTIVATION_THRESHOLD,
                )
            )
        } finally {
            recordMetric(request, startNanos, "api.listingreport.count")
        }
    }

    @Operation(summary = "Liste de tous les signalements (admin)")
    @GetMapping("${ListingReportScope.PROTECTED}/all", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findAll(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            adminAuthorization.requireAdmin()
            ApiResponse(service.findAll())
        } finally {
            recordMetric(request, startNanos, "api.listingreport.findalladmin")
        }
    }

    @Operation(summary = "Liste des signalements d'une annonce (admin)")
    @GetMapping("${ListingReportScope.PROTECTED}/{listingType}/{listingId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findByListing(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable listingType: OfferType,
        @PathVariable listingId: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            adminAuthorization.requireAdmin()
            ApiResponse(service.findByListing(listingType, listingId))
        } finally {
            recordMetric(request, startNanos, "api.listingreport.findall")
        }
    }

    private suspend fun requireUserId(): Long =
        auth.user()?.first?.userId ?: throw ResponseStatusException(
            HttpStatus.UNAUTHORIZED,
            "Authentification requise",
        )

    private fun recordMetric(request: HttpServletRequest, startNanos: Long, metricName: String) {
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
