package t3digitalgroup.vehnixauto.server.app.moto.infrastructure.controllers

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
import t3digitalgroup.vehnixauto.server.app.moto.application.services.MotoModelService
import t3digitalgroup.vehnixauto.server.app.moto.domain.models.request.MotoModelRequest
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.moto.MotoModelScope
import t3digitalgroup.vehnixauto.server.security.Auth
import t3digitalgroup.vehnixauto.server.security.monitoring.MetricModel
import t3digitalgroup.vehnixauto.server.security.monitoring.SentryService
import t3digitalgroup.vehnixauto.server.utils.ApiResponse

@Tag(name = "Moto Model", description = "Gestion des modèles de motos")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class MotoModelController(
    private val service: MotoModelService,
    private val auth: Auth,
    private val sentry: SentryService,
) {
    @Operation(summary = "Créer un modèle de moto")
    @PostMapping(MotoModelScope.PROTECTED, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun create(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: MotoModelRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val session = auth.user()
            val isAdmin = session?.second?.find { true } == true
            if (!isAdmin) {
                ResponseEntity.status(HttpStatus.FORBIDDEN).body(mapOf("message" to "Accès non autorisé"))
            } else {
                ResponseEntity.status(HttpStatus.CREATED).body(service.create(body))
            }
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.motomodel.create.count",
                    distributionName = "api.motomodel.create.latency",
                )
            )
        }
    }

    @Operation(summary = "Liste de tous les modèles de moto")
    @GetMapping(MotoModelScope.PUBLIC, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findAll(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.findAll().toList())
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.motomodel.findall.count",
                    distributionName = "api.motomodel.findall.latency",
                )
            )
        }
    }

    @Operation(summary = "Détail d'un modèle de moto")
    @GetMapping("${MotoModelScope.PUBLIC}/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
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
                    countName = "api.motomodel.findbyid.count",
                    distributionName = "api.motomodel.findbyid.latency",
                )
            )
        }
    }

    @Operation(summary = "Liste des modèles par marque")
    @GetMapping("${MotoModelScope.PUBLIC}/brand/{brand}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findByBrand(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable brand: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.findByBrand(brand).toList())
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.motomodel.findbybrand.count",
                    distributionName = "api.motomodel.findbybrand.latency",
                )
            )
        }
    }
}
