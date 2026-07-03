package t3digitalgroup.vehnixauto.server.app.message.infrastructure.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import t3digitalgroup.vehnixauto.server.app.message.application.services.*
import t3digitalgroup.vehnixauto.server.app.message.domain.models.request.CarListingMessageRequest
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.message.CarListingMessageScope
import t3digitalgroup.vehnixauto.server.security.monitoring.*
import t3digitalgroup.vehnixauto.server.utils.*

@Tag(name = "Car Listing Message", description = "Messages de chat liés aux annonces de voitures")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class CarListingMessageController(
    private val service: CarListingMessageService,
    private val sentry: SentryService,
) {
    @Operation(summary = "Envoyer un message dans une conversation")
    @PostMapping(CarListingMessageScope.PROTECTED, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun sendMessage(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: CarListingMessageRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ResponseEntity.status(HttpStatus.CREATED).body(service.sendMessage(body))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.carlistingmessage.sendmessage.count",
                    distributionName = "api.carlistingmessage.sendmessage.latency",
                )
            )
        }
    }

    @Operation(summary = "Messages d'une conversation")
    @GetMapping("${CarListingMessageScope.PROTECTED}/thread/{threadId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findByThreadId(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable threadId: Long,
        @RequestParam userId: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.findByThreadIdForUser(threadId, userId).toList())
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.carlistingmessage.findbythreadid.count",
                    distributionName = "api.carlistingmessage.findbythreadid.latency",
                )
            )
        }
    }

    @Operation(summary = "Marquer un message comme lu")
    @PatchMapping("${CarListingMessageScope.PROTECTED}/{id}/read", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun markAsRead(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
        @RequestParam userId: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ResponseEntity.ok(service.markAsRead(id, userId))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.carlistingmessage.markasread.count",
                    distributionName = "api.carlistingmessage.markasread.latency",
                )
            )
        }
    }
}
