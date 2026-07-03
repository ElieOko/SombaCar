package t3digitalgroup.vehnixauto.server.app.payment.infrastructure.controllers

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
import t3digitalgroup.vehnixauto.server.app.payment.application.services.DeviseService
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.Devise
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.payment.DeviseScope
import t3digitalgroup.vehnixauto.server.security.monitoring.MetricModel
import t3digitalgroup.vehnixauto.server.security.monitoring.SentryService
import t3digitalgroup.vehnixauto.server.utils.ApiResponse

@Tag(name = "Devise", description = "Gestion des devises")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class DeviseController(
    private val service: DeviseService,
    private val sentry: SentryService,
) {
    @Operation(summary = "Créer une devise")
    @PostMapping(DeviseScope.PROTECTED, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun create(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: Devise,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ResponseEntity.status(HttpStatus.CREATED).body(service.create(body))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.devise.create.count",
                    distributionName = "api.devise.create.latency",
                )
            )
        }
    }

    @Operation(summary = "Liste des devises")
    @GetMapping(DeviseScope.PUBLIC, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun getAllData(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.getAllData())
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.devise.getalldata.count",
                    distributionName = "api.devise.getalldata.latency",
                )
            )
        }
    }

    @Operation(summary = "Détail d'une devise")
    @GetMapping("${DeviseScope.PUBLIC}/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun getById(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ResponseEntity.ok(service.getById(id))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.devise.getbyid.count",
                    distributionName = "api.devise.getbyid.latency",
                )
            )
        }
    }
}
