package cl.duoc.pby2203.week7.events;

import java.util.UUID;
import java.time.Instant;

public record TransaccionRejectedEvent(
        UUID eventId, 
        String transaccionId, 
        Instant occurredAt,
        String reason
        ) 
implements DomainEvent {}