package cl.duoc.pby2203.week7.transaccion.api;

import jakarta.validation.constraints.NotBlank;

public record RejectTransaccionRequest(
        @NotBlank String reason
) {
}