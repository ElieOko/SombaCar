package t3digitalgroup.vehnixauto.server.utils

import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import t3digitalgroup.vehnixauto.server.security.monitoring.*


@Controller
@Profile(Mode.DEV)
class HomeController(
    private val sentry: SentryService,
) {
    @GetMapping("/")
    fun home(request: HttpServletRequest):String {
        val startNanos = System.nanoTime()
        try {
            return  "index"
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.home.home.count",
                    distributionName = "api.home.home.latency"
                )
            )
        }
    }
}