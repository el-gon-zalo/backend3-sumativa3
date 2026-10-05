package cl.duoc.pby2203.week7.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Hecho: una nueva reserva fue creada y quedó pendiente de completar la saga. */
public record TransaccionCreatedEvent(
        UUID eventId, 
        String transaccionId, 
        Instant occurredAt,
        String fecha, 
        Double monto, 
        String tipo
        ) 
implements DomainEvent {}
