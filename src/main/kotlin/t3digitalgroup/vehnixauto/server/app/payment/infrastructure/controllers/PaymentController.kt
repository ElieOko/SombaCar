package t3digitalgroup.vehnixauto.server.app.payment.infrastructure.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import kotlinx.coroutines.coroutineScope
import org.springframework.context.annotation.Profile
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import t3digitalgroup.vehnixauto.server.app.payment.application.services.PaymentService
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.Paiement
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.payment.PaymentScope
import t3digitalgroup.vehnixauto.server.security.monitoring.*
import t3digitalgroup.vehnixauto.server.utils.ApiResponse

@Tag(name = "Payment", description = "Gestion des paiements")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class PaymentController(
    private val service: PaymentService,
    private val sentry: SentryService,
) {
    @Operation(summary = "Créer un paiement")
    @PostMapping(PaymentScope.PROTECTED, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun create(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: Paiement,
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
                    countName = "api.payment.create.count",
                    distributionName = "api.payment.create.latency",
                )
            )
        }
    }

    @Operation(summary = "Callback de mise à jour du paiement")
    @PutMapping("${PaymentScope.PUBLIC}/callback", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun update(
        request: HttpServletRequest,
        @PathVariable version: String,
        @RequestParam reference: String,
        @RequestParam code: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            service.update(reference, code)
            ResponseEntity.ok(mapOf("message" to "Paiement mis à jour"))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.payment.update.count",
                    distributionName = "api.payment.update.latency",
                )
            )
        }
    }

    @Operation(summary = "Détail d'un paiement")
    @GetMapping("${PaymentScope.PROTECTED}/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun showDetail(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ResponseEntity.ok(service.showDetail(id))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.payment.showdetail.count",
                    distributionName = "api.payment.showdetail.latency",
                )
            )
        }
    }

    @Operation(summary = "Liste de tous les paiements")
    @GetMapping(PaymentScope.PROTECTED, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun showAll(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.showAll())
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.payment.showall.count",
                    distributionName = "api.payment.showall.latency",
                )
            )
        }
    }

    @Operation(summary = "Paiements d'un utilisateur")
    @GetMapping("${PaymentScope.PROTECTED}/user/{userId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun owner(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable userId: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ResponseEntity.ok(service.owner(userId))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.payment.owner.count",
                    distributionName = "api.payment.owner.latency",
                )
            )
        }
    }
}
