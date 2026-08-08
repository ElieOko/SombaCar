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
import t3digitalgroup.vehnixauto.server.app.payment.application.services.PaymentService
import t3digitalgroup.vehnixauto.server.app.payment.application.services.PurchasePaymentService
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.Paiement
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.TransactionCallBack
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.TransactionCardRequest
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.TransactionRequest
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.payment.PaymentScope
import t3digitalgroup.vehnixauto.server.security.AdminAuthorization
import t3digitalgroup.vehnixauto.server.security.Auth
import t3digitalgroup.vehnixauto.server.security.monitoring.MetricModel
import t3digitalgroup.vehnixauto.server.security.monitoring.SentryService
import t3digitalgroup.vehnixauto.server.utils.ApiResponse

@Tag(name = "Payment", description = "Gestion des paiements")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class PaymentController(
    private val service: PaymentService,
    private val purchasePaymentService: PurchasePaymentService,
    private val auth: Auth,
    private val adminAuthorization: AdminAuthorization,
    private val sentry: SentryService,
) {
    @Operation(summary = "Payer une offre via Mobile Money")
    @PostMapping("${PaymentScope.PROTECTED}/purchase/mobile", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun purchaseMobileMoney(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: TransactionRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            ResponseEntity.ok(purchasePaymentService.payMobileMoney(userId, body))
        } finally {
            recordMetric(request, startNanos, "api.payment.purchase.mobile")
        }
    }

    @Operation(summary = "Payer une offre via carte bancaire")
    @PostMapping("${PaymentScope.PROTECTED}/purchase/card", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun purchaseCard(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: TransactionCardRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            ResponseEntity.ok(purchasePaymentService.payCard(userId, body))
        } finally {
            recordMetric(request, startNanos, "api.payment.purchase.card")
        }
    }

    @Operation(summary = "Callback Mobile Money FlexPay")
    @PostMapping("${PaymentScope.PUBLIC}/mobile/callback", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun mobileCallback(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: TransactionCallBack,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            if (!body.reference.isNullOrBlank() && !body.code.isNullOrBlank()) {
                purchasePaymentService.handleCallback(body.reference, body.code)
            }
            ResponseEntity.ok(mapOf("message" to "Callback traité"))
        } finally {
            recordMetric(request, startNanos, "api.payment.mobile.callback")
        }
    }

    @Operation(summary = "Callback carte FlexPay")
    @PostMapping("${PaymentScope.PUBLIC}/card/callback", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun cardCallback(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: TransactionCallBack,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            if (!body.reference.isNullOrBlank() && !body.code.isNullOrBlank()) {
                purchasePaymentService.handleCallback(body.reference, body.code)
            }
            ResponseEntity.ok(mapOf("message" to "Callback traité"))
        } finally {
            recordMetric(request, startNanos, "api.payment.card.callback")
        }
    }

    @Operation(summary = "Créer un paiement manuel")
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
            recordMetric(request, startNanos, "api.payment.create")
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
            recordMetric(request, startNanos, "api.payment.showdetail")
        }
    }

    @Operation(summary = "Liste de tous les paiements (admin)")
    @GetMapping(PaymentScope.PROTECTED, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun showAll(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            adminAuthorization.requireAdmin()
            ApiResponse(service.showAll())
        } finally {
            recordMetric(request, startNanos, "api.payment.showall")
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
            val session = auth.user()
            val isAdmin = session?.second?.any { it } == true
            val currentUserId = session?.first?.userId
            if (!isAdmin && currentUserId != userId) {
                ResponseEntity.status(HttpStatus.FORBIDDEN).body(mapOf("message" to "Accès non autorisé"))
            } else {
                ResponseEntity.ok(service.owner(userId))
            }
        } finally {
            recordMetric(request, startNanos, "api.payment.owner")
        }
    }

    private suspend fun requireUserId(): Long =
        auth.user()?.first?.userId ?: throw org.springframework.web.server.ResponseStatusException(
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
