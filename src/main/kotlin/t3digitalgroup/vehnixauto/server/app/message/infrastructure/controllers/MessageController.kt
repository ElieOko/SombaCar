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
import org.springframework.web.multipart.MultipartHttpServletRequest
import t3digitalgroup.vehnixauto.server.app.message.application.services.MessageService
import t3digitalgroup.vehnixauto.server.app.message.domain.models.request.PlatformReplyRequest
import t3digitalgroup.vehnixauto.server.app.message.domain.models.request.SupportMessageRequest
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.message.MessageScope
import t3digitalgroup.vehnixauto.server.security.monitoring.MetricModel
import t3digitalgroup.vehnixauto.server.security.monitoring.SentryService
import t3digitalgroup.vehnixauto.server.utils.ApiResponse
import t3digitalgroup.vehnixauto.server.utils.bufferMultipartFile

@Tag(name = "Message", description = "Gestion des messages de support")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class MessageController(
    private val service: MessageService,
    private val sentry: SentryService,
) {
    @Operation(summary = "Envoyer un message utilisateur (texte et/ou photos)")
    @PostMapping(
        "${MessageScope.PROTECTED}/user/media",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    suspend fun sendUserMessageWithMedia(
        request: HttpServletRequest,
        @PathVariable version: String,
        multipartRequest: MultipartHttpServletRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val threadId = multipartRequest.getParameter("threadId")?.toLongOrNull()
                ?: throw org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "threadId est obligatoire",
                )
            val senderId = multipartRequest.getParameter("senderId")?.toLongOrNull()
            val content = multipartRequest.getParameter("content")
            val files = multipartRequest.getFiles("files")
                .filter { !it.isEmpty }
                .map(::bufferMultipartFile)
            ResponseEntity.status(HttpStatus.CREATED).body(
                service.sendUserMessage(
                    SupportMessageRequest(threadId = threadId, senderId = senderId, content = content),
                    files,
                )
            )
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.message.sendusermessage.media.count",
                    distributionName = "api.message.sendusermessage.media.latency",
                )
            )
        }
    }

    @Operation(summary = "Répondre en tant que plateforme (texte et/ou photos)")
    @PostMapping(
        "${MessageScope.PROTECTED}/platform/media",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    suspend fun replyAsPlatformWithMedia(
        request: HttpServletRequest,
        @PathVariable version: String,
        multipartRequest: MultipartHttpServletRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val threadId = multipartRequest.getParameter("threadId")?.toLongOrNull()
                ?: throw org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "threadId est obligatoire",
                )
            val adminId = multipartRequest.getParameter("adminId")?.toLongOrNull()
                ?: throw org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "adminId est obligatoire",
                )
            val content = multipartRequest.getParameter("content")
            val files = multipartRequest.getFiles("files")
                .filter { !it.isEmpty }
                .map(::bufferMultipartFile)
            ResponseEntity.status(HttpStatus.CREATED).body(
                service.replyAsPlatform(
                    PlatformReplyRequest(threadId = threadId, adminId = adminId, content = content),
                    files,
                )
            )
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.message.replyasplatform.media.count",
                    distributionName = "api.message.replyasplatform.media.latency",
                )
            )
        }
    }

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
