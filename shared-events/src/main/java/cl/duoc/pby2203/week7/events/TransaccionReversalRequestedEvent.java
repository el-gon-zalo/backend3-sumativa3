package cl.duoc.pby2203.week7.events;

import java.util.UUID;
import java.time.Instant;

public record TransaccionReversalRequestedEvent(
        UUID eventId, 
        String transaccionId, 
        Instant occurredAt,
        Double monto, 
        String reason
        ) 
implements DomainEvent {}