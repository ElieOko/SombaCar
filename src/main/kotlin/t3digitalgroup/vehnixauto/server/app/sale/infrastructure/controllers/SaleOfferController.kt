package t3digitalgroup.vehnixauto.server.app.sale.infrastructure.controllers

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
import t3digitalgroup.vehnixauto.server.app.sale.application.services.SaleOfferService
import t3digitalgroup.vehnixauto.server.app.sale.domain.models.request.SaleOfferRequest
import t3digitalgroup.vehnixauto.server.app.sale.domain.models.request.SaleOfferUpdateRequest
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.sale.SaleOfferScope
import t3digitalgroup.vehnixauto.server.security.AdminAuthorization
import t3digitalgroup.vehnixauto.server.security.monitoring.MetricModel
import t3digitalgroup.vehnixauto.server.security.monitoring.SentryService
import t3digitalgroup.vehnixauto.server.utils.ApiResponse
import t3digitalgroup.vehnixauto.server.utils.OfferType

@Tag(name = "Sale Offer", description = "Offres de vente créées par l'admin")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class SaleOfferController(
    private val service: SaleOfferService,
    private val adminAuthorization: AdminAuthorization,
    private val sentry: SentryService,
) {
    @Operation(summary = "Créer une offre de vente (admin)")
    @PostMapping(SaleOfferScope.PROTECTED, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun create(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: SaleOfferRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val adminId = adminAuthorization.requireAdmin()
            ResponseEntity.status(HttpStatus.CREATED).body(service.create(body, adminId))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.saleoffer.create.count",
                    distributionName = "api.saleoffer.create.latency",
                )
            )
        }
    }

    @Operation(summary = "Mettre à jour une offre (admin)")
    @PutMapping("${SaleOfferScope.PROTECTED}/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun update(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
        @Valid @RequestBody body: SaleOfferUpdateRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            adminAuthorization.requireAdmin()
            ResponseEntity.ok(service.update(id, body))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.saleoffer.update.count",
                    distributionName = "api.saleoffer.update.latency",
                )
            )
        }
    }

    @Operation(summary = "Désactiver une offre (admin)")
    @PatchMapping("${SaleOfferScope.PROTECTED}/{id}/deactivate", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun deactivate(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            adminAuthorization.requireAdmin()
            ResponseEntity.ok(service.deactivate(id))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.saleoffer.deactivate.count",
                    distributionName = "api.saleoffer.deactivate.latency",
                )
            )
        }
    }

    @Operation(summary = "Liste des offres actives")
    @GetMapping(SaleOfferScope.PUBLIC, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findAllActive(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.findAllActive())
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.saleoffer.findall.count",
                    distributionName = "api.saleoffer.findall.latency",
                )
            )
        }
    }

    @Operation(summary = "Liste complète des offres (admin)")
    @GetMapping("${SaleOfferScope.PROTECTED}/all", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findAll(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            adminAuthorization.requireAdmin()
            ApiResponse(service.findAll())
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.saleoffer.findalladmin.count",
                    distributionName = "api.saleoffer.findalladmin.latency",
                )
            )
        }
    }

    @Operation(summary = "Détail d'une offre")
    @GetMapping("${SaleOfferScope.PUBLIC}/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
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
                    countName = "api.saleoffer.findbyid.count",
                    distributionName = "api.saleoffer.findbyid.latency",
                )
            )
        }
    }

    @Operation(summary = "Offres par type")
    @GetMapping("${SaleOfferScope.PUBLIC}/type/{offerType}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findByType(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable offerType: OfferType,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.findActiveByType(offerType))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.saleoffer.findbytype.count",
                    distributionName = "api.saleoffer.findbytype.latency",
                )
            )
        }
    }
}
