package t3digitalgroup.vehnixauto.server.app.favorite.infrastructure.controllers

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
import t3digitalgroup.vehnixauto.server.app.favorite.application.services.ListingFavoriteService
import t3digitalgroup.vehnixauto.server.app.favorite.domain.models.request.ListingFavoriteRequest
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.favorite.FavoriteScope
import t3digitalgroup.vehnixauto.server.security.Auth
import t3digitalgroup.vehnixauto.server.security.monitoring.MetricModel
import t3digitalgroup.vehnixauto.server.security.monitoring.SentryService
import t3digitalgroup.vehnixauto.server.utils.ApiResponse
import t3digitalgroup.vehnixauto.server.utils.OfferType

@Tag(name = "Favorite", description = "Favoris des annonces (voiture, moto, pièce)")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class ListingFavoriteController(
    private val service: ListingFavoriteService,
    private val auth: Auth,
    private val sentry: SentryService,
) {
    @Operation(summary = "Ajouter une annonce aux favoris")
    @PostMapping(FavoriteScope.PROTECTED, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun add(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: ListingFavoriteRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            ResponseEntity.status(HttpStatus.CREATED).body(service.add(userId, body))
        } finally {
            recordMetric(request, startNanos, "api.favorite.add")
        }
    }

    @Operation(summary = "Retirer une annonce des favoris")
    @DeleteMapping("${FavoriteScope.PROTECTED}/{listingType}/{listingId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun remove(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable listingType: OfferType,
        @PathVariable listingId: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            service.remove(userId, listingType, listingId)
            ResponseEntity.ok(mapOf("message" to "Annonce retirée des favoris"))
        } finally {
            recordMetric(request, startNanos, "api.favorite.remove")
        }
    }

    @Operation(summary = "Liste de tous mes favoris")
    @GetMapping(FavoriteScope.PROTECTED, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findAll(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            ApiResponse(service.findByUserId(userId))
        } finally {
            recordMetric(request, startNanos, "api.favorite.findall")
        }
    }

    @Operation(summary = "Mes favoris par type (CAR, MOTO, PART)")
    @GetMapping("${FavoriteScope.PROTECTED}/type/{listingType}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findByType(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable listingType: OfferType,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            ApiResponse(service.findByUserIdAndType(userId, listingType))
        } finally {
            recordMetric(request, startNanos, "api.favorite.findbytype")
        }
    }

    @Operation(summary = "Vérifier si une annonce est en favoris")
    @GetMapping("${FavoriteScope.PROTECTED}/{listingType}/{listingId}/check", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun check(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable listingType: OfferType,
        @PathVariable listingId: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            ResponseEntity.ok(
                mapOf(
                    "listingType" to listingType.name,
                    "listingId" to listingId,
                    "isFavorite" to service.isFavorite(userId, listingType, listingId),
                )
            )
        } finally {
            recordMetric(request, startNanos, "api.favorite.check")
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
