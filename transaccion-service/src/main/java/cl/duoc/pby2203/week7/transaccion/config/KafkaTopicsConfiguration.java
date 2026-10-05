package cl.duoc.pby2203.week7.transaccion.config;

import cl.duoc.pby2203.week7.events.TopicNames;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Crea todos los tópicos del laboratorio al iniciar booking-service.
 *
 * <p>Cada tópico se crea con 3 particiones. Las particiones son la unidad que Kafka
 * distribuye entre consumidores de un mismo consumer group, por lo que permiten
 * demostrar paralelismo levantando varias instancias de un consumidor.</p>
 */
@Configuration
public class KafkaTopicsConfiguration {

    private NewTopic topic(String name) {
        return TopicBuilder.name(name)
                .partitions(3)
                .replicas(1) // Laboratorio con un único broker.
                .build();
    }

    @Bean NewTopic transaccionCreated() { return topic(TopicNames.TRANSACCION_CREATED); }
    @Bean NewTopic transaccionPending() { return topic(TopicNames.TRANSACCION_PENDING); }
    @Bean NewTopic transaccionConfirmed() { return topic(TopicNames.TRANSACCION_CONFIRMED); }
    @Bean NewTopic transaccionCancelled() { return topic(TopicNames.TRANSACCION_CANCELLED); }
    @Bean NewTopic transaccionApproved() { return topic(TopicNames.TRANSACCION_APPROVED); }
    @Bean NewTopic transaccionRejected() { return topic(TopicNames.TRANSACCION_REJECTED); }
    @Bean NewTopic transaccionReversalRequested() { return topic(TopicNames.TRANSACCION_REVERSAL_REQUESTED); }
}
