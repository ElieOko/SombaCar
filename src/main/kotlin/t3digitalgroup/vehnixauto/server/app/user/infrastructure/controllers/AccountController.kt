package t3digitalgroup.vehnixauto.server.app.user.infrastructure.controllers

import io.swagger.v3.oas.annotations.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.*
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import jakarta.servlet.http.*
import t3digitalgroup.vehnixauto.server.app.user.application.services.*
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.entities.AccountDTO
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.account.AccountScope
import t3digitalgroup.vehnixauto.server.security.monitoring.*

@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class AccountController(
    private val service: AccountService,
    private val sentry: SentryService,
) {
    @Operation(summary = "List of accounts")
    @GetMapping(AccountScope.PUBLIC,produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun getAllAccountE(request: HttpServletRequest, @PathVariable version: String): Map<String, List<AccountDTO>> = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            mapOf("accounts" to service.getAll().toList())
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.account.getallaccounte.count",
                    distributionName = "api.account.getallaccounte.latency"
                )
            )
        }
    }
}