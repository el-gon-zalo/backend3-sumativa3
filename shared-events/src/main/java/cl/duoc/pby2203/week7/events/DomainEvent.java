package cl.duoc.pby2203.week7.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Contrato mínimo que comparten todos los eventos del laboratorio.
 *
 * <p>Un evento representa un hecho que YA ocurrió. Por eso los nombres de los
 * eventos se escriben en pasado: BookingCreated, SeatReserved, PaymentApproved, etc.</p>
 *
 * <p>El {@code eventId} es especialmente importante porque nos permite detectar
 * duplicados. En arquitecturas distribuidas es normal recibir el mismo mensaje más
 * de una vez cuando existen reintentos o semántica at-least-once.</p>
 */

public interface DomainEvent {
    UUID eventId();
    String transaccionId();
    Instant occurredAt();
}
