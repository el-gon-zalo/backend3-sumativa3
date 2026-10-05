package cl.duoc.pby2203.week7.transaccion.messaging;

import cl.duoc.pby2203.week7.events.*;
import cl.duoc.pby2203.week7.transaccion.domain.Transaccion;
import cl.duoc.pby2203.week7.transaccion.repository.TransaccionRepository;
import cl.duoc.pby2203.week7.events.TopicNames;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * Participación de transaccion-service en una Saga por COREOGRAFÍA.
 *
 * <p>No hay orquestador central: el servicio reacciona a hechos publicados por otros
 * servicios, cambia su estado y emite nuevos eventos.</p>
 */
@Component
public class TransaccionSagaListener {
    private static final Logger log = LoggerFactory.getLogger(TransaccionSagaListener.class);

    private final TransaccionRepository repository;
    private final ProcessedEventStore processedEvents;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TransaccionSagaListener(TransaccionRepository repository,
                                   ProcessedEventStore processedEvents,
                                   KafkaTemplate<String, Object> kafkaTemplate) {
        this.repository = repository;
        this.processedEvents = processedEvents;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = TopicNames.TRANSACCION_APPROVED, groupId = "transaccion-service-group")
    public void onTransaccionApproved(TransaccionApprovedEvent event) {
        if (!processedEvents.isFirstTime(event.eventId())) {
            log.warn("[IDEMPOTENCIA] TransaccionApproved duplicado ignorado eventId={}", event.eventId());
            return;
        }

        Transaccion transaccion = find(event.transaccionId());
        transaccion.confirm();
        repository.save(transaccion);

        TransaccionConfirmedEvent confirmed = new TransaccionConfirmedEvent(
                UUID.randomUUID(), transaccion.getTransaccionId(), Instant.now(),
                transaccion.getFecha(), transaccion.getMonto(), transaccion.getTipo()
        );
        publish(TopicNames.TRANSACCION_CONFIRMED, confirmed);
        log.info("[SAGA] Transacción CONFIRMADA id={}", transaccion.getTransaccionId());
    }

    @KafkaListener(topics = TopicNames.TRANSACCION_REJECTED, groupId = "transaccion-service-group")
    public void onTransaccionRejected(TransaccionRejectedEvent event) {
        if (!processedEvents.isFirstTime(event.eventId())) {
            log.warn("[IDEMPOTENCIA] TransaccionRejected duplicado ignorado eventId={}", event.eventId());
            return;
        }

        Transaccion transaccion = find(event.transaccionId());
        transaccion.cancel(event.reason());
        repository.save(transaccion);
        publishCancellation(transaccion, event.reason());

        /*
         * COMPENSACIÓN DE SAGA:
         * El efecto previo de la transacción (por ejemplo, el monto retenido) debe
         * deshacerse, por eso se solicita un reverso.
         */
        TransaccionReversalRequestedEvent reversal = new TransaccionReversalRequestedEvent(
                UUID.randomUUID(), transaccion.getTransaccionId(), Instant.now(),
                transaccion.getMonto(), "Transaction rejected: " + event.reason()
        );
        publish(TopicNames.TRANSACCION_REVERSAL_REQUESTED, reversal);
        log.info("[SAGA-COMPENSACION] Solicitado reverso de transacción id={}", transaccion.getTransaccionId());
    }

    private void publishCancellation(Transaccion transaccion, String reason) {
        TransaccionCancelledEvent cancelled = new TransaccionCancelledEvent(
                UUID.randomUUID(), transaccion.getTransaccionId(), Instant.now(), reason
        );
        publish(TopicNames.TRANSACCION_CANCELLED, cancelled);
        log.info("[SAGA] Transacción CANCELADA id={} motivo={}", transaccion.getTransaccionId(), reason);
    }

    private Transaccion find(String transaccionId) {
        return repository.findById(transaccionId)
                .orElseThrow(() -> new IllegalStateException("Transaccion not found: " + transaccionId));
    }

    private void publish(String topic, DomainEvent event) {
        kafkaTemplate.send(topic, event.transaccionId(), event);
    }
}