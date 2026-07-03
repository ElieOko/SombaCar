package t3digitalgroup.vehnixauto.server.app.user.infrastructure.controllers

import io.swagger.v3.oas.annotations.*
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.*
import jakarta.validation.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import t3digitalgroup.vehnixauto.server.app.user.application.services.UserService
import t3digitalgroup.vehnixauto.server.app.user.domain.models.request.UserRequestChange
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.security.monitoring.*
import t3digitalgroup.vehnixauto.server.utils.ApiResponse

@Tag(name = "User", description = "Managements Users")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
class UserController(
    private val userService : UserService,
    private val sentry : SentryService
) {
    @Operation(summary = "List of users")
    @GetMapping("/protected/users")
    suspend fun getListUser(request: HttpServletRequest, @PathVariable version: String) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(userService.findAllUser().toList())
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.user.getlistuser.count",
                    distributionName = "api.user.getlistuser.latency"
                )
            )
        }
    }

    @Operation(summary = "Detail user")
    @GetMapping("/protected/users/{id}")
    suspend fun getUser(
        request: HttpServletRequest,
        @PathVariable id:Long,
        @PathVariable version:String
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ResponseEntity.ok().body(userService.findIdUser(id))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.user.getuser.count",
                    distributionName = "api.user.getuser.latency"
                )
            )
        }
    }

    @Operation(summary = "Modification utilisateur")
    @PutMapping("/protected/users/{id}")
    suspend fun updateUser(
        request: HttpServletRequest,
        @PathVariable("id") userId : Long,
        @RequestBody @Valid user : UserRequestChange,
        @PathVariable version: String
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ResponseEntity.ok(userService.updateUser(userId, user))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.user.updateuser.count",
                    distributionName = "api.user.updateuser.latency"
                )
            )
        }
    }

    @GetMapping("/private/users")
    suspend fun getAllUserPrivate(request: HttpServletRequest, @PathVariable version: String) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(userService.findAllUser().toList())
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.user.getAllUserPrivate.count",
                    distributionName = "api.user.getAllUserPrivate.latency"
                )
            )
        }
    }
}
