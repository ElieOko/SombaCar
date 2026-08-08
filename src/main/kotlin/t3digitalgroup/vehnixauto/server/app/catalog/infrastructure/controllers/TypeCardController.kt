package t3digitalgroup.vehnixauto.server.app.catalog.infrastructure.controllers

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
import t3digitalgroup.vehnixauto.server.app.catalog.application.services.TypeCardService
import t3digitalgroup.vehnixauto.server.app.catalog.domain.models.TypeCard
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.catalog.TypeCardScope
import t3digitalgroup.vehnixauto.server.security.AdminAuthorization
import t3digitalgroup.vehnixauto.server.security.monitoring.MetricModel
import t3digitalgroup.vehnixauto.server.security.monitoring.SentryService
import t3digitalgroup.vehnixauto.server.utils.ApiResponse

@Tag(name = "Type Card", description = "Types de cartes et documents")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class TypeCardController(
    private val service: TypeCardService,
    private val adminAuthorization: AdminAuthorization,
    private val sentry: SentryService,
) {
    @Operation(summary = "Liste des types de cartes")
    @GetMapping(TypeCardScope.PUBLIC, produces = [MediaType.APPLICATION_JSON_VALUE])
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
                    countName = "api.typecard.findall.count",
                    distributionName = "api.typecard.findall.latency",
                )
            )
        }
    }

    @Operation(summary = "Créer un type de carte (admin)")
    @PostMapping(TypeCardScope.PROTECTED, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun create(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: TypeCard,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            adminAuthorization.requireAdmin()
            ResponseEntity.status(HttpStatus.CREATED).body(service.create(body))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.typecard.create.count",
                    distributionName = "api.typecard.create.latency",
                )
            )
        }
    }

    @Operation(summary = "Mettre à jour un type de carte (admin)")
    @PutMapping("${TypeCardScope.PROTECTED}/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun update(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
        @Valid @RequestBody body: TypeCard,
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
                    countName = "api.typecard.update.count",
                    distributionName = "api.typecard.update.latency",
                )
            )
        }
    }

    @Operation(summary = "Supprimer un type de carte (admin)")
    @DeleteMapping("${TypeCardScope.PROTECTED}/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun delete(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            adminAuthorization.requireAdmin()
            service.delete(id)
            ResponseEntity.ok(mapOf("message" to "Type de carte supprimé"))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.typecard.delete.count",
                    distributionName = "api.typecard.delete.latency",
                )
            )
        }
    }
}
