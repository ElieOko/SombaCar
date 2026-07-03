package t3digitalgroup.vehnixauto.server.app.car.infrastructure.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import t3digitalgroup.vehnixauto.server.app.car.application.services.CarModelService
import t3digitalgroup.vehnixauto.server.app.car.domain.models.request.CarModelRequest
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.car.CarModelScope
import t3digitalgroup.vehnixauto.server.security.Auth
import t3digitalgroup.vehnixauto.server.security.monitoring.*
import t3digitalgroup.vehnixauto.server.utils.*

@Tag(name = "Car Model", description = "Gestion des modèles de voitures")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class CarModelController(
    private val service: CarModelService,
    private val auth: Auth,
    private val sentry: SentryService,
) {
    @Operation(summary = "Créer un modèle de voiture")
    @PostMapping(CarModelScope.PROTECTED, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun create(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: CarModelRequest,
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
                    countName = "api.carmodel.create.count",
                    distributionName = "api.carmodel.create.latency",
                )
            )
        }
    }

    @Operation(summary = "Liste de tous les modèles de voiture")
    @GetMapping(CarModelScope.PUBLIC, produces = [MediaType.APPLICATION_JSON_VALUE])
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
                    countName = "api.carmodel.findall.count",
                    distributionName = "api.carmodel.findall.latency",
                )
            )
        }
    }

    @Operation(summary = "Détail d'un modèle de voiture")
    @GetMapping("${CarModelScope.PUBLIC}/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
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
                    countName = "api.carmodel.findbyid.count",
                    distributionName = "api.carmodel.findbyid.latency",
                )
            )
        }
    }

    @Operation(summary = "Liste des modèles par marque")
    @GetMapping("${CarModelScope.PUBLIC}/brand/{brand}", produces = [MediaType.APPLICATION_JSON_VALUE])
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
                    countName = "api.carmodel.findbybrand.count",
                    distributionName = "api.carmodel.findbybrand.latency",
                )
            )
        }
    }
}
