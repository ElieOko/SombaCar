package t3digitalgroup.vehnixauto.server.app.car.infrastructure.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import kotlinx.coroutines.coroutineScope
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import t3digitalgroup.vehnixauto.server.app.car.application.services.DocumentService
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.car.DocumentScope
import t3digitalgroup.vehnixauto.server.security.monitoring.MetricModel
import t3digitalgroup.vehnixauto.server.security.monitoring.SentryService
import t3digitalgroup.vehnixauto.server.utils.ApiResponse

@Tag(name = "Document", description = "Répertoire des documents véhicule")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class DocumentController(
    private val service: DocumentService,
    private val sentry: SentryService,
) {
    @Operation(summary = "Liste de tous les documents")
    @GetMapping(DocumentScope.PUBLIC, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findAll(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.findAll())
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.document.findall.count",
                    distributionName = "api.document.findall.latency",
                )
            )
        }
    }

    @Operation(summary = "Détail d'un document")
    @GetMapping("${DocumentScope.PUBLIC}/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
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
                    countName = "api.document.findbyid.count",
                    distributionName = "api.document.findbyid.latency",
                )
            )
        }
    }
}
