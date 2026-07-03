package t3digitalgroup.vehnixauto.server.bootstrap

import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import t3digitalgroup.vehnixauto.server.bootstrap.command.SeedCommand
import t3digitalgroup.vehnixauto.server.utils.Mode

@Component
@Profile(Mode.DEV)
class DatabaseSeedOrchestrator(
    private val commands: List<SeedCommand>,
    @Value("\${app.seed.enabled:true}") private val seedEnabled: Boolean,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() = runBlocking {
        if (!seedEnabled) {
            log.info("Database seeding is disabled.")
            return@runBlocking
        }
        commands.sortedBy { it.order }.forEach { command ->
            log.info("Executing seed command: {}", command::class.simpleName)
            command.execute()
        }
        log.info("Database seeding completed.")
    }
}
