package cl.duoc.pby2203.week7.events;

import java.time.Instant;
import java.util.UUID;

/** Hecho final exitoso de la saga: la reserva quedó confirmada. */
public record TransaccionConfirmedEvent(
        UUID eventId, 
        String transaccionId, 
        Instant occurredAt,
        String fecha, 
        Double monto, 
        String tipo
        ) 
implements DomainEvent {}
