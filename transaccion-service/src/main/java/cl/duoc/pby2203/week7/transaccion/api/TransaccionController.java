package cl.duoc.pby2203.week7.transaccion.api;

import cl.duoc.pby2203.week7.transaccion.domain.Transaccion;
import cl.duoc.pby2203.week7.transaccion.repository.TransaccionRepository;
import cl.duoc.pby2203.week7.transaccion.service.TransaccionApplicationService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    private final TransaccionApplicationService service;
    private final TransaccionRepository repository;

    public TransaccionController(
            TransaccionApplicationService service,
            TransaccionRepository repository) {

        this.service = service;
        this.repository = repository;
    }

    /**
     * Crear una nueva transacción.
     *
     * POST /api/transacciones
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
            @Valid @RequestBody CreateTransaccionRequest request) {

        Transaccion transaccion = service.create(request);

        /*
         * 202 Accepted comunica que la solicitud fue aceptada,
         * pero el procesamiento asíncrono continúa mediante Kafka.
         */
        return ResponseEntity.accepted()
                .location(
                        URI.create(
                                "/api/transacciones/"
                                        + transaccion.getTransaccionId()
                        )
                )
                .body(
                        Map.of(
                                "transaccionId",
                                transaccion.getTransaccionId(),
                                "status",
                                transaccion.getStatus()
                        )
                );
    }

    /**
     * Obtener una transacción por ID.
     *
     * GET /api/transacciones/{transaccionId}
     */
    @GetMapping("/{transaccionId}")
    public ResponseEntity<Transaccion> find(
            @PathVariable String transaccionId) {

        return repository.findById(transaccionId)
                .map(ResponseEntity::ok)
                .orElseGet(
                        () -> ResponseEntity.notFound().build()
                );
    }

    /**
     * Obtener todas las transacciones.
     *
     * GET /api/transacciones
     */
    @GetMapping
    public Collection<Transaccion> findAll() {

        return repository.findAll();
    }

    /**
     * Solicitar aprobación de una transacción.
     *
     * POST /api/transacciones/{transaccionId}/approve
     */
    @PostMapping("/{transaccionId}/approve")
    public ResponseEntity<Map<String, String>> approve(
            @PathVariable String transaccionId) {

        service.approve(transaccionId);

        return ResponseEntity.accepted()
                .body(
                        Map.of(
                                "transaccionId",
                                transaccionId,
                                "message",
                                "TransaccionApproved event published"
                        )
                );
    }

    /**
     * Solicitar rechazo de una transacción.
     *
     * POST /api/transacciones/{transaccionId}/reject
     */
    @PostMapping("/{transaccionId}/reject")
    public ResponseEntity<Map<String, String>> reject(
            @PathVariable String transaccionId,
            @Valid @RequestBody RejectTransaccionRequest request) {

        service.reject(
                transaccionId,
                request.reason()
        );

        return ResponseEntity.accepted()
                .body(
                        Map.of(
                                "transaccionId",
                                transaccionId,
                                "message",
                                "TransaccionRejected event published"
                        )
                );
    }

    /**
     * Republicar el evento TransaccionCreated original.
     *
     * POST /api/transacciones/{transaccionId}/replay-created
     */
    @PostMapping("/{transaccionId}/replay-created")
    public ResponseEntity<Map<String, String>> replay(
            @PathVariable String transaccionId) {

        service.replayCreated(transaccionId);

        return ResponseEntity.accepted()
                .body(
                        Map.of(
                                "message",
                                "TransaccionCreated replayed with the original eventId"
                        )
                );
    }
}