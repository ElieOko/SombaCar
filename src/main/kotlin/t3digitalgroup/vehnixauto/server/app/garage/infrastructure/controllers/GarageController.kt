package t3digitalgroup.vehnixauto.server.app.garage.infrastructure.controllers

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
import t3digitalgroup.vehnixauto.server.app.garage.application.services.GarageService
import t3digitalgroup.vehnixauto.server.app.garage.domain.models.request.GarageRequest
import t3digitalgroup.vehnixauto.server.app.garage.domain.models.request.GarageUpdateRequest
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.garage.GarageScope
import t3digitalgroup.vehnixauto.server.security.Auth
import t3digitalgroup.vehnixauto.server.security.PremiumAuthorization
import t3digitalgroup.vehnixauto.server.security.monitoring.MetricModel
import t3digitalgroup.vehnixauto.server.security.monitoring.SentryService
import t3digitalgroup.vehnixauto.server.utils.ApiResponse
import t3digitalgroup.vehnixauto.server.utils.GarageType
import t3digitalgroup.vehnixauto.server.utils.GeoCoordinatesRequest

@Tag(name = "Garage", description = "Gestion des garages (voiture, moto, pièce)")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class GarageController(
    private val service: GarageService,
    private val auth: Auth,
    private val premiumAuthorization: PremiumAuthorization,
    private val sentry: SentryService,
) {
    @Operation(summary = "Créer un garage")
    @PostMapping(GarageScope.PROTECTED, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun create(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: GarageRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            ResponseEntity.status(HttpStatus.CREATED).body(service.create(userId, body))
        } finally {
            recordMetric(request, startNanos, "api.garage.create")
        }
    }

    @Operation(summary = "Mes garages actifs")
    @GetMapping("${GarageScope.PROTECTED}/mine/active", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findMineActive(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            ApiResponse(service.findActiveByUserId(userId))
        } finally {
            recordMetric(request, startNanos, "api.garage.mineactive")
        }
    }

    @Operation(summary = "Tous mes garages")
    @GetMapping("${GarageScope.PROTECTED}/mine", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findMine(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            ApiResponse(service.findAllByUserId(userId))
        } finally {
            recordMetric(request, startNanos, "api.garage.mine")
        }
    }

    @Operation(summary = "Détail d'un garage (premium)")
    @GetMapping("${GarageScope.PROTECTED}/browse/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findById(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            premiumAuthorization.requirePremium()
            ResponseEntity.ok(service.findById(id))
        } finally {
            recordMetric(request, startNanos, "api.garage.findbyid")
        }
    }

    @Operation(summary = "Liste des garages actifs (premium)")
    @GetMapping("${GarageScope.PROTECTED}/browse", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findAllActive(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            premiumAuthorization.requirePremium()
            ApiResponse(service.findAllActive())
        } finally {
            recordMetric(request, startNanos, "api.garage.findall")
        }
    }

    @Operation(summary = "Garages actifs par type (premium)")
    @GetMapping("${GarageScope.PROTECTED}/browse/type/{garageType}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findByType(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable garageType: GarageType,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            premiumAuthorization.requirePremium()
            ApiResponse(service.findActiveByType(garageType))
        } finally {
            recordMetric(request, startNanos, "api.garage.findbytype")
        }
    }

    @Operation(summary = "Mettre à jour les coordonnées géographiques d'un garage")
    @PatchMapping("${GarageScope.PROTECTED}/{id}/coordinates", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun updateCoordinates(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
        @Valid @RequestBody body: GeoCoordinatesRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            ResponseEntity.ok(service.updateCoordinates(userId, id, body))
        } finally {
            recordMetric(request, startNanos, "api.garage.coordinates")
        }
    }

    @Operation(summary = "Mettre à jour un garage")
    @PutMapping("${GarageScope.PROTECTED}/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun update(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
        @Valid @RequestBody body: GarageUpdateRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            ResponseEntity.ok(service.update(userId, id, body))
        } finally {
            recordMetric(request, startNanos, "api.garage.update")
        }
    }

    @Operation(summary = "Désactiver un garage")
    @PatchMapping("${GarageScope.PROTECTED}/{id}/deactivate", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun deactivate(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            ResponseEntity.ok(service.deactivate(userId, id))
        } finally {
            recordMetric(request, startNanos, "api.garage.deactivate")
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
