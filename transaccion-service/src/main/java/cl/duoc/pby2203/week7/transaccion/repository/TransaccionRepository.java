package cl.duoc.pby2203.week7.transaccion.repository;

import cl.duoc.pby2203.week7.transaccion.domain.Transaccion;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Repositorio en memoria. ConcurrentHashMap permite acceso seguro desde hilos HTTP y Kafka. */
@Repository
public class TransaccionRepository {
    private final ConcurrentHashMap<String, Transaccion> data = new ConcurrentHashMap<>();

    public Transaccion save(Transaccion transaccion) {
        data.put(transaccion.getTransaccionId(), transaccion);
        return transaccion;
    }

    public Optional<Transaccion> findById(String id) {
        return Optional.ofNullable(data.get(id));
    }

    public Collection<Transaccion> findAll() {
        return data.values();
    }
}
