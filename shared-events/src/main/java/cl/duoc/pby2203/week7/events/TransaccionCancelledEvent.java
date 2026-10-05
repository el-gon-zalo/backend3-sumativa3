package cl.duoc.pby2203.week7.events;

import java.time.Instant;
import java.util.UUID;

/** Hecho final fallido de la saga: la reserva fue cancelada. */

public record TransaccionCancelledEvent(
        UUID eventId, 
        String transaccionId, 
        Instant occurredAt,
        String reason
        ) 
implements DomainEvent {}
