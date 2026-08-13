package t3digitalgroup.vehnixauto.server.app.subscription.infrastructure.controllers

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
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.TransactionCallBack
import t3digitalgroup.vehnixauto.server.app.subscription.application.services.SubscriptionPaymentService
import t3digitalgroup.vehnixauto.server.app.subscription.application.services.SubscriptionPlanService
import t3digitalgroup.vehnixauto.server.app.subscription.application.services.UserSubscriptionService
import t3digitalgroup.vehnixauto.server.app.subscription.domain.models.request.SubscriptionCardPaymentRequest
import t3digitalgroup.vehnixauto.server.app.subscription.domain.models.request.SubscriptionPaymentRequest
import t3digitalgroup.vehnixauto.server.app.subscription.domain.models.request.SubscriptionPlanRequest
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.payment.PaymentScope
import t3digitalgroup.vehnixauto.server.route.subscription.SubscriptionPlanScope
import t3digitalgroup.vehnixauto.server.route.subscription.SubscriptionScope
import t3digitalgroup.vehnixauto.server.security.AdminAuthorization
import t3digitalgroup.vehnixauto.server.security.Auth
import t3digitalgroup.vehnixauto.server.security.monitoring.MetricModel
import t3digitalgroup.vehnixauto.server.security.monitoring.SentryService
import t3digitalgroup.vehnixauto.server.utils.ApiResponse

@Tag(name = "Subscription", description = "Abonnements premium")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class SubscriptionController(
    private val planService: SubscriptionPlanService,
    private val userSubscriptionService: UserSubscriptionService,
    private val subscriptionPaymentService: SubscriptionPaymentService,
    private val auth: Auth,
    private val adminAuthorization: AdminAuthorization,
    private val sentry: SentryService,
) {
    @Operation(summary = "Plans d'abonnement disponibles")
    @GetMapping(SubscriptionPlanScope.PUBLIC, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findPlans(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(planService.findAllActive())
        } finally {
            recordMetric(request, startNanos, "api.subscription.plans")
        }
    }

    @Operation(summary = "Créer un plan d'abonnement (admin)")
    @PostMapping(SubscriptionPlanScope.PROTECTED, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun createPlan(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: SubscriptionPlanRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            adminAuthorization.requireAdmin()
            ResponseEntity.status(HttpStatus.CREATED).body(planService.create(body))
        } finally {
            recordMetric(request, startNanos, "api.subscription.createplan")
        }
    }

    @Operation(summary = "Statut premium de l'utilisateur")
    @GetMapping("${SubscriptionScope.PROTECTED}/status", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun status(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            ResponseEntity.ok(userSubscriptionService.getStatus(userId))
        } finally {
            recordMetric(request, startNanos, "api.subscription.status")
        }
    }

    @Operation(summary = "Historique des abonnements")
    @GetMapping("${SubscriptionScope.PROTECTED}/history", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun history(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            ApiResponse(userSubscriptionService.findHistory(userId))
        } finally {
            recordMetric(request, startNanos, "api.subscription.history")
        }
    }

    @Operation(summary = "Payer un abonnement via Mobile Money")
    @PostMapping("${PaymentScope.PROTECTED}/subscription/mobile", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun payMobileMoney(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: SubscriptionPaymentRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            ResponseEntity.ok(subscriptionPaymentService.payMobileMoney(userId, body))
        } finally {
            recordMetric(request, startNanos, "api.subscription.paymobile")
        }
    }

    @Operation(summary = "Payer un abonnement via carte")
    @PostMapping("${PaymentScope.PROTECTED}/subscription/card", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun payCard(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: SubscriptionCardPaymentRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            ResponseEntity.ok(subscriptionPaymentService.payCard(userId, body))
        } finally {
            recordMetric(request, startNanos, "api.subscription.paycard")
        }
    }

    @Operation(summary = "Callback Mobile Money abonnement")
    @PostMapping("${PaymentScope.PUBLIC}/subscription/mobile/callback", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun mobileCallback(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: TransactionCallBack,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            if (!body.reference.isNullOrBlank() && !body.code.isNullOrBlank()) {
                subscriptionPaymentService.handleCallback(body.reference, body.code)
            }
            ResponseEntity.ok(mapOf("message" to "Callback traité"))
        } finally {
            recordMetric(request, startNanos, "api.subscription.mobilecallback")
        }
    }

    @Operation(summary = "Callback carte abonnement")
    @PostMapping("${PaymentScope.PUBLIC}/subscription/card/callback", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun cardCallback(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: TransactionCallBack,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            if (!body.reference.isNullOrBlank() && !body.code.isNullOrBlank()) {
                subscriptionPaymentService.handleCallback(body.reference, body.code)
            }
            ResponseEntity.ok(mapOf("message" to "Callback traité"))
        } finally {
            recordMetric(request, startNanos, "api.subscription.cardcallback")
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
