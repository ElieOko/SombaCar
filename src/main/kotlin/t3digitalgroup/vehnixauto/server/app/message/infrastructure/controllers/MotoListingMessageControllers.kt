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
import t3digitalgroup.vehnixauto.server.app.message.domain.models.request.MotoListingMessageRequest
import t3digitalgroup.vehnixauto.server.app.message.domain.models.request.MotoListingThreadRequest
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.message.MotoListingMessageScope
import t3digitalgroup.vehnixauto.server.route.message.MotoListingThreadScope
import t3digitalgroup.vehnixauto.server.security.monitoring.MetricModel
import t3digitalgroup.vehnixauto.server.security.monitoring.SentryService
import t3digitalgroup.vehnixauto.server.utils.ApiResponse
import t3digitalgroup.vehnixauto.server.utils.bufferMultipartFile

@Tag(name = "Moto Listing Thread", description = "Conversations liées aux annonces de motos")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class MotoListingThreadController(
    private val service: MotoListingThreadService,
    private val sentry: SentryService,
) {
    @Operation(summary = "Contacter le propriétaire d'une annonce moto")
    @PostMapping(MotoListingThreadScope.PROTECTED, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun openThread(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: MotoListingThreadRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ResponseEntity.status(HttpStatus.CREATED).body(service.openThread(body))
        } finally {
            recordMetric(request, startNanos, "api.motolistingthread.openthread")
        }
    }

    @Operation(summary = "Détail d'une conversation moto")
    @GetMapping("${MotoListingThreadScope.PROTECTED}/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findById(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ResponseEntity.ok(service.findById(id))
        } finally {
            recordMetric(request, startNanos, "api.motolistingthread.findbyid")
        }
    }

    @Operation(summary = "Conversations moto d'un utilisateur")
    @GetMapping("${MotoListingThreadScope.PROTECTED}/user/{userId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findByUserId(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable userId: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.findByParticipantUserId(userId).toList())
        } finally {
            recordMetric(request, startNanos, "api.motolistingthread.findbyuserid")
        }
    }

    @Operation(summary = "Conversations liées à une annonce moto")
    @GetMapping("${MotoListingThreadScope.PROTECTED}/listing/{motoListingId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findByMotoListingId(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable motoListingId: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.findByMotoListingId(motoListingId).toList())
        } finally {
            recordMetric(request, startNanos, "api.motolistingthread.findbylistingid")
        }
    }

    @Operation(summary = "Clôturer une conversation moto")
    @PatchMapping("${MotoListingThreadScope.PROTECTED}/{id}/status", produces = [MediaType.APPLICATION_JSON_VALUE])
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
            recordMetric(request, startNanos, "api.motolistingthread.updatestatus")
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

@Tag(name = "Moto Listing Message", description = "Messages de chat liés aux annonces de motos")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class MotoListingMessageController(
    private val service: MotoListingMessageService,
    private val sentry: SentryService,
) {
    @Operation(summary = "Envoyer un message moto (texte et/ou photos)")
    @PostMapping(
        MotoListingMessageScope.PROTECTED,
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
                    MotoListingMessageRequest(threadId = threadId, senderId = senderId, content = content),
                    files,
                )
            )
        } finally {
            recordMetric(request, startNanos, "api.motolistingmessage.sendmessagemedia")
        }
    }

    @Operation(summary = "Envoyer un message moto")
    @PostMapping(MotoListingMessageScope.PROTECTED, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun sendMessage(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: MotoListingMessageRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ResponseEntity.status(HttpStatus.CREATED).body(service.sendMessage(body))
        } finally {
            recordMetric(request, startNanos, "api.motolistingmessage.sendmessage")
        }
    }

    @Operation(summary = "Messages d'une conversation moto")
    @GetMapping("${MotoListingMessageScope.PROTECTED}/thread/{threadId}", produces = [MediaType.APPLICATION_JSON_VALUE])
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
            recordMetric(request, startNanos, "api.motolistingmessage.findbythreadid")
        }
    }

    @Operation(summary = "Marquer un message moto comme lu")
    @PatchMapping("${MotoListingMessageScope.PROTECTED}/{id}/read", produces = [MediaType.APPLICATION_JSON_VALUE])
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
            recordMetric(request, startNanos, "api.motolistingmessage.markasread")
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
