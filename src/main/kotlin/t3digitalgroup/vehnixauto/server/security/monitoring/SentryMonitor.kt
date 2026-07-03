package t3digitalgroup.vehnixauto.server.security.monitoring

import io.sentry.*
import org.springframework.beans.factory.annotation.*
import org.springframework.stereotype.*
import jakarta.annotation.*

@Component
class SentryInitializer(
    @Value("\${sentry.dsn}") private val dsn: String
) {
    @PostConstruct
    fun init() {
        Sentry.init { options ->
            options.dsn = dsn
        }
    }
}
