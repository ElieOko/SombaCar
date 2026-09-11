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
import t3digitalgroup.vehnixauto.server.app.message.application.services.*
import t3digitalgroup.vehnixauto.server.app.message.domain.models.CarListingThreadStatus
import t3digitalgroup.vehnixauto.server.app.message.domain.models.request.PartListingMessageRequest
import t3digitalgroup.vehnixauto.server.app.message.domain.models.request.PartListingThreadRequest
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.message.PartListingMessageScope
import t3digitalgroup.vehnixauto.server.route.message.PartListingThreadScope
import t3digitalgroup.vehnixauto.server.security.monitoring.MetricModel
import t3digitalgroup.vehnixauto.server.security.monitoring.SentryService
import t3digitalgroup.vehnixauto.server.utils.ApiResponse
import t3digitalgroup.vehnixauto.server.utils.bufferMultipartFile

@Tag(name = "Part Listing Thread", description = "Conversations liées aux annonces de pièces")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class PartListingThreadController(
    private val service: PartListingThreadService,
    private val sentry: SentryService,
) {
    @Operation(summary = "Contacter le propriétaire d'une annonce pièce")
    @PostMapping(PartListingThreadScope.PROTECTED, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun openThread(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: PartListingThreadRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ResponseEntity.status(HttpStatus.CREATED).body(service.openThread(body))
        } finally {
            recordMetric(request, startNanos, "api.partlistingthread.openthread")
        }
    }

    @Operation(summary = "Détail d'une conversation pièce")
    @GetMapping("${PartListingThreadScope.PROTECTED}/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findById(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ResponseEntity.ok(service.findById(id))
        } finally {
            recordMetric(request, startNanos, "api.partlistingthread.findbyid")
        }
    }

    @Operation(summary = "Conversations pièce d'un utilisateur")
    @GetMapping("${PartListingThreadScope.PROTECTED}/user/{userId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findByUserId(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable userId: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.findByParticipantUserId(userId).toList())
        } finally {
            recordMetric(request, startNanos, "api.partlistingthread.findbyuserid")
        }
    }

    @Operation(summary = "Conversations liées à une annonce pièce")
    @GetMapping("${PartListingThreadScope.PROTECTED}/listing/{partListingId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findByPartListingId(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable partListingId: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.findByPartListingId(partListingId).toList())
        } finally {
            recordMetric(request, startNanos, "api.partlistingthread.findbylistingid")
        }
    }

    @Operation(summary = "Clôturer une conversation pièce")
    @PatchMapping("${PartListingThreadScope.PROTECTED}/{id}/status", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun updateStatus(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
        @RequestParam status: CarListingThreadStatus,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ResponseEntity.ok(service.updateStatus(id, status))
        } finally {
            recordMetric(request, startNanos, "api.partlistingthread.updatestatus")
        }
    }

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

@Tag(name = "Part Listing Message", description = "Messages de chat liés aux annonces de pièces")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class PartListingMessageController(
    private val service: PartListingMessageService,
    private val sentry: SentryService,
) {
    @Operation(summary = "Envoyer un message pièce (texte et/ou photos)")
    @PostMapping(
        PartListingMessageScope.PROTECTED,
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    suspend fun sendMessageWithMedia(
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
                ?: throw org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "senderId est obligatoire",
                )
            val content = multipartRequest.getParameter("content")
            val files = multipartRequest.getFiles("files")
                .filter { !it.isEmpty }
                .map(::bufferMultipartFile)
            ResponseEntity.status(HttpStatus.CREATED).body(
                service.sendMessage(
                    PartListingMessageRequest(threadId = threadId, senderId = senderId, content = content),
                    files,
                )
            )
        } finally {
            recordMetric(request, startNanos, "api.partlistingmessage.sendmessagemedia")
        }
    }

    @Operation(summary = "Envoyer un message pièce")
    @PostMapping(PartListingMessageScope.PROTECTED, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun sendMessage(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: PartListingMessageRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ResponseEntity.status(HttpStatus.CREATED).body(service.sendMessage(body))
        } finally {
            recordMetric(request, startNanos, "api.partlistingmessage.sendmessage")
        }
    }

    @Operation(summary = "Messages d'une conversation pièce")
    @GetMapping("${PartListingMessageScope.PROTECTED}/thread/{threadId}", produces = [MediaType.APPLICATION_JSON_VALUE])
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
            recordMetric(request, startNanos, "api.partlistingmessage.findbythreadid")
        }
    }

    @Operation(summary = "Marquer un message pièce comme lu")
    @PatchMapping("${PartListingMessageScope.PROTECTED}/{id}/read", produces = [MediaType.APPLICATION_JSON_VALUE])
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
            recordMetric(request, startNanos, "api.partlistingmessage.markasread")
        }
    }

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
