package t3digitalgroup.vehnixauto.server.app.user.infrastructure.controllers

import io.swagger.v3.oas.annotations.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.*
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import jakarta.servlet.http.*
import t3digitalgroup.vehnixauto.server.app.user.application.services.*
import t3digitalgroup.vehnixauto.server.app.user.domain.models.TypeAccount
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.account.AccountTypeScope
import t3digitalgroup.vehnixauto.server.security.monitoring.*
import t3digitalgroup.vehnixauto.server.utils.ApiResponse

@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class TypeAccountController(
    private val service: TypeAccountService,
    private val sentry: SentryService,
) {
    @Operation(summary = "List Of TypeAccounts")
    @GetMapping(AccountTypeScope.PUBLIC,produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun getAllTypeAccountE(request: HttpServletRequest, @PathVariable version: String): ApiResponse<List<TypeAccount>> = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.getAll().toList())
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.typeaccount.getalltypeaccounte.count",
                    distributionName = "api.typeaccount.getalltypeaccounte.latency"
                )
            )
        }
    }
}