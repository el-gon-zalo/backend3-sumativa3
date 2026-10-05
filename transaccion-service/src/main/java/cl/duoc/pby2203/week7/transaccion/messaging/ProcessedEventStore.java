package cl.duoc.pby2203.week7.transaccion.messaging;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ejemplo deliberadamente simple de idempotencia.
 *
 * <p>Kafka puede entregar un evento más de una vez. Guardamos los eventId ya procesados
 * y solo ejecutamos el efecto de negocio si el id aparece por primera vez.</p>
 *
 * <p>En producción este registro debería persistirse en una base de datos o almacenamiento
 * compartido para sobrevivir reinicios y funcionar con varias instancias.</p>
 */
@Component
public class ProcessedEventStore {
    private final Set<UUID> processed = ConcurrentHashMap.newKeySet();

    public boolean isFirstTime(UUID eventId) {
        return processed.add(eventId);
    }
}
