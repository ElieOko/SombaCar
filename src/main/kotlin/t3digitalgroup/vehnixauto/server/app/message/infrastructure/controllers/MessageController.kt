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
import t3digitalgroup.vehnixauto.server.app.message.application.services.MessageService
import t3digitalgroup.vehnixauto.server.app.message.domain.models.request.PlatformReplyRequest
import t3digitalgroup.vehnixauto.server.app.message.domain.models.request.SupportMessageRequest
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.message.MessageScope
import t3digitalgroup.vehnixauto.server.security.monitoring.MetricModel
import t3digitalgroup.vehnixauto.server.security.monitoring.SentryService
import t3digitalgroup.vehnixauto.server.utils.ApiResponse

@Tag(name = "Message", description = "Gestion des messages de support")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class MessageController(
    private val service: MessageService,
    private val sentry: SentryService,
) {
    @Operation(summary = "Envoyer un message utilisateur")
    @PostMapping("${MessageScope.PROTECTED}/user", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun sendUserMessage(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: SupportMessageRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ResponseEntity.status(HttpStatus.CREATED).body(service.sendUserMessage(body))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.message.sendusermessage.count",
                    distributionName = "api.message.sendusermessage.latency",
                )
            )
        }
    }

    @Operation(summary = "Répondre en tant que plateforme")
    @PostMapping("${MessageScope.PROTECTED}/platform", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun replyAsPlatform(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: PlatformReplyRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ResponseEntity.status(HttpStatus.CREATED).body(service.replyAsPlatform(body))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.message.replyasplatform.count",
                    distributionName = "api.message.replyasplatform.latency",
                )
            )
        }
    }

    @Operation(summary = "Messages d'un fil de discussion")
    @GetMapping("${MessageScope.PROTECTED}/thread/{threadId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findByThreadId(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable threadId: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.findByThreadId(threadId).toList())
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.message.findbythreadid.count",
                    distributionName = "api.message.findbythreadid.latency",
                )
            )
        }
    }

    @Operation(summary = "Marquer un message comme lu")
    @PatchMapping("${MessageScope.PROTECTED}/{id}/read", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun markAsRead(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ResponseEntity.ok(service.markAsRead(id))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.message.markasread.count",
                    distributionName = "api.message.markasread.latency",
                )
            )
        }
    }
}
