package cl.duoc.pby2203.week7.events;

/**
 * Nombres de tópicos centralizados para evitar strings repetidos en los servicios.
 *
 * <p>Los tópicos describen hechos del dominio. Mantener los nombres en un único lugar
 * facilita leer el código y evita errores de escritura entre productores y consumidores.</p>
 */
public final class TopicNames {
    private TopicNames() {}

    public static final String TRANSACCION_CREATED = "transaccion.created";
    public static final String TRANSACCION_PENDING = "transaccion.pending";
    public static final String TRANSACCION_CONFIRMED = "transaccion.confirmed";
    public static final String TRANSACCION_CANCELLED = "transaccion.cancelled";
    public static final String TRANSACCION_APPROVED = "transaccion.approved"; 
    public static final String TRANSACCION_REJECTED = "transaccion.rejected"; 
    public static final String TRANSACCION_REVERSAL_REQUESTED = "transaccion.reversal-requested"; 
}