package t3digitalgroup.vehnixauto.server.app.notification.infrastructure.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import kotlinx.coroutines.coroutineScope
import org.springframework.context.annotation.Profile
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import t3digitalgroup.vehnixauto.server.app.notification.application.services.NotificationSystemService
import t3digitalgroup.vehnixauto.server.app.notification.domain.models.NotificationSystem
import t3digitalgroup.vehnixauto.server.app.notification.domain.models.request.NotificationSystemRequest
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.notification.NotificationScope
import t3digitalgroup.vehnixauto.server.security.monitoring.*
import t3digitalgroup.vehnixauto.server.utils.ApiResponse

@Tag(name = "Notification", description = "Gestion des notifications système")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class NotificationController(
    private val service: NotificationSystemService,
    private val sentry: SentryService,
) {
    @Operation(summary = "Créer une notification système")
    @PostMapping(NotificationScope.PROTECTED, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun create(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: NotificationSystemRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val notice = NotificationSystem(
                title = body.title,
                description = body.description,
            )
            ResponseEntity.status(HttpStatus.CREATED).body(service.create(notice))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.notification.create.count",
                    distributionName = "api.notification.create.latency",
                )
            )
        }
    }

    @Operation(summary = "Notifications d'un utilisateur")
    @GetMapping("${NotificationScope.PROTECTED}/user/{userId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun notificationByUser(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable userId: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.notificationByUser(userId))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.notification.notificationbyuser.count",
                    distributionName = "api.notification.notificationbyuser.latency",
                )
            )
        }
    }

    @Operation(summary = "Désactiver une notification")
    @PatchMapping("${NotificationScope.PROTECTED}/{id}/disable", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun notificationDisable(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            service.notificationDisable(id)
            ResponseEntity.ok(mapOf("message" to "Notification désactivée"))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.notification.notificationdisable.count",
                    distributionName = "api.notification.notificationdisable.latency",
                )
            )
        }
    }
}
