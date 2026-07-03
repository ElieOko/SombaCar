package t3digitalgroup.vehnixauto.server.security.monitoring

import io.sentry.*
import io.sentry.metrics.*
import org.springframework.stereotype.*

@Service
class SentryService {
    fun callToMetric(metric : MetricModel){
        val durationMs = (System.nanoTime() - metric.startNanos) / 1_000_000.0
        val metricParams = SentryMetricsParameters.create(
            mapOf("route" to metric.route, "status" to metric.status)
        )
        Sentry.metrics().count(metric.countName, 1.0, "request", metricParams)
        Sentry.metrics().distribution(
            metric.distributionName,
            durationMs,
            MetricsUnit.Duration.MILLISECOND,
            metricParams
        )
    }
}

data class MetricModel(
    val startNanos : Long,
    val route : String,
    val status: String,
    val countName : String = "api.property.get_all.count",
    val distributionName : String = "api.property.get_all.latency"
)
