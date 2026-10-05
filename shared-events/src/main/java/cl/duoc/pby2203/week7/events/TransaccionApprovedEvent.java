package cl.duoc.pby2203.week7.events;

import java.util.UUID;
import java.time.Instant;

public record TransaccionApprovedEvent(
        UUID eventId, 
        String transaccionId, 
        Instant occurredAt
        ) 
implements DomainEvent {}
