package t3digitalgroup.vehnixauto.server.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import t3digitalgroup.vehnixauto.server.app.tracking.domain.models.TrackingKafkaEvent
import t3digitalgroup.vehnixauto.server.utils.Mode

@Configuration
@EnableKafka
@Profile(Mode.DEV)
@ConditionalOnProperty(name = ["app.kafka.enabled"], havingValue = "true", matchIfMissing = false)
class KafkaConfig(
    @Value("\${spring.kafka.bootstrap-servers:localhost:9092}")
    private val bootstrapServers: String,
) {
    @Bean
    fun trackingProducerFactory(): ProducerFactory<String, TrackingKafkaEvent> {
        val props = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
        )
        return DefaultKafkaProducerFactory(props)
    }

    @Bean
    fun trackingKafkaTemplate(): KafkaTemplate<String, TrackingKafkaEvent> =
        KafkaTemplate(trackingProducerFactory())

    @Bean
    fun trackingConsumerFactory(): ConsumerFactory<String, TrackingKafkaEvent> {
        val props = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to "vehnix-tracking",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
            JsonDeserializer.TRUSTED_PACKAGES to "*",
            JsonDeserializer.VALUE_DEFAULT_TYPE to TrackingKafkaEvent::class.java.name,
        )
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun trackingKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, TrackingKafkaEvent> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, TrackingKafkaEvent>()
        factory.setConsumerFactory(trackingConsumerFactory())
        return factory
    }
}
