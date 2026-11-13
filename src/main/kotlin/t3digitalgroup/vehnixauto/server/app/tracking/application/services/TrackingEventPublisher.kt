package t3digitalgroup.vehnixauto.server.app.tracking.application.services

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Profile
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import t3digitalgroup.vehnixauto.server.app.tracking.domain.models.TrackingKafkaEvent
import t3digitalgroup.vehnixauto.server.app.tracking.domain.models.VehicleTrackingEvent
import t3digitalgroup.vehnixauto.server.route.tracking.TrackingTopics
import t3digitalgroup.vehnixauto.server.utils.Mode

@Component
@Profile(Mode.DEV)
class WebSocketTrackingPublisher(
    private val messagingTemplate: SimpMessagingTemplate,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun publish(event: VehicleTrackingEvent) {
        val destination = "${TrackingTopics.VEHICLE}/${event.vehicleType.name.lowercase()}/${event.vehicleId}"
        messagingTemplate.convertAndSend(destination, event)
        log.debug("WS tracking published to {}", destination)
    }
}

@Component
@Profile(Mode.DEV)
@ConditionalOnProperty(name = ["app.kafka.enabled"], havingValue = "true")
class KafkaTrackingPublisher(
    private val kafkaTemplate: KafkaTemplate<String, TrackingKafkaEvent>,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun publish(event: VehicleTrackingEvent) {
        val key = "${event.vehicleType.name}-${event.vehicleId}"
        kafkaTemplate.send(TrackingTopics.KAFKA_VEHICLE_TRACKING, key, TrackingKafkaEvent(payload = event))
        log.debug("Kafka tracking published key={}", key)
    }
}

@Component
@Profile(Mode.DEV)
class TrackingEventBridge(
    private val webSocketPublisher: WebSocketTrackingPublisher,
    private val kafkaPublisherProvider: ObjectProvider<KafkaTrackingPublisher>,
) {
    fun broadcast(event: VehicleTrackingEvent) {
        webSocketPublisher.publish(event)
        kafkaPublisherProvider.ifAvailable?.publish(event)
    }
}
