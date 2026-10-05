package cl.duoc.pby2203.week7.transaccion.domain;

import java.time.Instant;
import java.util.UUID;

public class Transaccion {
    private String transaccionId;
    private String fecha;
    private Double monto;
    private String tipo;
    private final Instant createdAt;
    private final UUID transaccionCreatedEventId;

    private TransaccionStatus status;
    private String failureReason;

    public Transaccion(String transaccionId, String fecha, Double monto, String tipo,
                       UUID transaccionCreatedEventId) {
        this.transaccionId = transaccionId;
        this.fecha = fecha;
        this.monto = monto;
        this.tipo = tipo;
        this.transaccionCreatedEventId = transaccionCreatedEventId;
        this.createdAt = Instant.now();
        this.status = TransaccionStatus.PENDING;
    }

   public String getTransaccionId() { return transaccionId; }
    public void setTransaccionId(String transaccionId) { this.transaccionId = transaccionId; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Instant getCreatedAt() { return createdAt; }
    public UUID getTransaccionCreatedEventId() { return transaccionCreatedEventId; }
    public TransaccionStatus getStatus() { return status; }
    public String getFailureReason() { return failureReason; }

    public void confirm() {
        this.status = TransaccionStatus.CONFIRMED;
        this.failureReason = null;
    }

    public void cancel(String reason) {
        this.status = TransaccionStatus.CANCELLED;
        this.failureReason = reason;
    }
}