package cl.duoc.pby2203.week7.transaccion.service;

import cl.duoc.pby2203.week7.events.DomainEvent;
import cl.duoc.pby2203.week7.events.TopicNames;
import cl.duoc.pby2203.week7.events.TransaccionApprovedEvent;
import cl.duoc.pby2203.week7.events.TransaccionCreatedEvent;
import cl.duoc.pby2203.week7.events.TransaccionRejectedEvent;
import cl.duoc.pby2203.week7.transaccion.api.CreateTransaccionRequest;
import cl.duoc.pby2203.week7.transaccion.domain.Transaccion;
import cl.duoc.pby2203.week7.transaccion.repository.TransaccionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class TransaccionApplicationService {

    private static final Logger log =
            LoggerFactory.getLogger(TransaccionApplicationService.class);

    private final TransaccionRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TransaccionApplicationService(
            TransaccionRepository repository,
            KafkaTemplate<String, Object> kafkaTemplate) {

        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Crea una nueva transacción.
     *
     * La transacción queda inicialmente en PENDING
     * y se publica el evento TransaccionCreated.
     */
    public Transaccion create(CreateTransaccionRequest request) {

        String transaccionId = UUID.randomUUID().toString();
        UUID eventId = UUID.randomUUID();

        Transaccion transaccion = new Transaccion(
                transaccionId,
                request.fecha(),
                request.monto(),
                request.tipo(),
                eventId
        );

        repository.save(transaccion);

        TransaccionCreatedEvent event = new TransaccionCreatedEvent(
                eventId,
                transaccionId,
                Instant.now(),
                request.fecha(),
                request.monto(),
                request.tipo()
        );

        publish(TopicNames.TRANSACCION_CREATED, event);

        log.info(
                "[PRODUCER] TransaccionCreated publicado transaccionId={} eventId={}",
                transaccionId,
                eventId
        );

        return transaccion;
    }

    /**
     * Solicita la aprobación de una transacción.
     *
     * Este método NO cambia directamente el estado.
     * Publica un TransaccionApprovedEvent.
     *
     * El TransaccionSagaListener será quien procese
     * el evento y cambie el estado a CONFIRMED.
     */
    public void approve(String transaccionId) {

        Transaccion transaccion = find(transaccionId);

        UUID eventId = UUID.randomUUID();

        TransaccionApprovedEvent event = new TransaccionApprovedEvent(
                eventId,
                transaccion.getTransaccionId(),
                Instant.now()
        );

        publish(TopicNames.TRANSACCION_APPROVED, event);

        log.info(
                "[PRODUCER] TransaccionApproved publicado transaccionId={} eventId={}",
                transaccionId,
                eventId
        );
    }

    /**
     * Solicita el rechazo de una transacción.
     *
     * Este método NO cambia directamente el estado.
     * Publica un TransaccionRejectedEvent.
     *
     * El TransaccionSagaListener será quien procese
     * el evento y realice la compensación.
     */
    public void reject(String transaccionId, String reason) {

        Transaccion transaccion = find(transaccionId);

        UUID eventId = UUID.randomUUID();

        TransaccionRejectedEvent event = new TransaccionRejectedEvent(
                eventId,
                transaccion.getTransaccionId(),
                Instant.now(),
                reason
        );

        publish(TopicNames.TRANSACCION_REJECTED, event);

        log.info(
                "[PRODUCER] TransaccionRejected publicado transaccionId={} eventId={} reason={}",
                transaccionId,
                eventId,
                reason
        );
    }

    /**
     * Republica el evento TransaccionCreated original.
     *
     * El eventId original se mantiene deliberadamente para
     * demostrar el comportamiento frente a eventos duplicados.
     */
    public void replayCreated(String transaccionId) {

        Transaccion transaccion = repository.findById(transaccionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Transaccion not found: " + transaccionId
                        )
                );

        TransaccionCreatedEvent event = new TransaccionCreatedEvent(
                transaccion.getTransaccionCreatedEventId(),
                transaccion.getTransaccionId(),
                transaccion.getCreatedAt(),
                transaccion.getFecha(),
                transaccion.getMonto(),
                transaccion.getTipo()
        );

        publish(TopicNames.TRANSACCION_CREATED, event);

        log.warn(
                "[DEMO DUPLICADO] TransaccionCreated republicado con el MISMO eventId={}",
                event.eventId()
        );
    }

    /**
     * Busca una transacción existente.
     */
    private Transaccion find(String transaccionId) {

        return repository.findById(transaccionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Transaccion not found: " + transaccionId
                        )
                );
    }

    /**
     * Publica un evento de dominio en Kafka.
     */
    private void publish(String topic, DomainEvent event) {

        kafkaTemplate.send(
                topic,
                event.transaccionId(),
                event
        );
    }
}