package t3digitalgroup.vehnixauto.server.app.cart.infrastructure.controllers

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
import t3digitalgroup.vehnixauto.server.app.cart.application.services.CartService
import t3digitalgroup.vehnixauto.server.app.cart.domain.models.request.CartItemRequest
import t3digitalgroup.vehnixauto.server.app.cart.domain.models.request.CartItemUpdateRequest
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.cart.CartScope
import t3digitalgroup.vehnixauto.server.security.Auth
import t3digitalgroup.vehnixauto.server.security.monitoring.MetricModel
import t3digitalgroup.vehnixauto.server.security.monitoring.SentryService
import t3digitalgroup.vehnixauto.server.utils.ApiResponse

@Tag(name = "Cart", description = "Gestion du panier utilisateur")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class CartController(
    private val service: CartService,
    private val auth: Auth,
    private val sentry: SentryService,
) {
    @Operation(summary = "Ajouter une pièce au panier")
    @PostMapping(CartScope.PROTECTED, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun addItem(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: CartItemRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            ResponseEntity.status(HttpStatus.CREATED).body(service.addItem(userId, body))
        } finally {
            recordMetric(request, startNanos, "api.cart.add")
        }
    }

    @Operation(summary = "Panier actif de l'utilisateur")
    @GetMapping("${CartScope.PROTECTED}/active", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findActive(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            ApiResponse(service.findActiveByUserId(userId))
        } finally {
            recordMetric(request, startNanos, "api.cart.active")
        }
    }

    @Operation(summary = "Historique du panier (articles payés ou retirés)")
    @GetMapping("${CartScope.PROTECTED}/inactive", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findInactive(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            ApiResponse(service.findInactiveByUserId(userId))
        } finally {
            recordMetric(request, startNanos, "api.cart.inactive")
        }
    }

    @Operation(summary = "Résumé du panier avec total converti")
    @GetMapping("${CartScope.PROTECTED}/summary", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun summary(
        request: HttpServletRequest,
        @PathVariable version: String,
        @RequestParam deviseId: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            ResponseEntity.ok(service.getSummary(userId, deviseId))
        } finally {
            recordMetric(request, startNanos, "api.cart.summary")
        }
    }

    @Operation(summary = "Mettre à jour la quantité d'un article")
    @PutMapping("${CartScope.PROTECTED}/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun updateQuantity(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
        @Valid @RequestBody body: CartItemUpdateRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            ResponseEntity.ok(service.updateQuantity(userId, id, body))
        } finally {
            recordMetric(request, startNanos, "api.cart.update")
        }
    }

    @Operation(summary = "Retirer un article du panier actif")
    @PatchMapping("${CartScope.PROTECTED}/{id}/deactivate", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun deactivateItem(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            ResponseEntity.ok(service.deactivateItem(userId, id))
        } finally {
            recordMetric(request, startNanos, "api.cart.deactivate")
        }
    }

    @Operation(summary = "Vider le panier actif")
    @PatchMapping("${CartScope.PROTECTED}/clear", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun clearCart(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            ApiResponse(service.clearActiveCart(userId))
        } finally {
            recordMetric(request, startNanos, "api.cart.clear")
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
