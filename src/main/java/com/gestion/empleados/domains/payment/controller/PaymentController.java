package com.gestion.empleados.domains.payment.controller;

import com.gestion.empleados.domains.payment.dto.request.PaymentDTOin;
import com.gestion.empleados.domains.payment.dto.response.PaymentDTO;
import com.gestion.empleados.domains.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment Endpoints")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Crea un pago", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<PaymentDTO> create(@Valid @RequestBody PaymentDTOin dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza el período o método de pago", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<PaymentDTO> update(@PathVariable Long id, @Valid @RequestBody PaymentDTOin dto) {
        return ResponseEntity.ok(paymentService.update(id, dto));
    }

    @PatchMapping("/{id}/pay")
    @Operation(summary = "Marca el pago como pagado", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<PaymentDTO> pay(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.pay(id));
    }

    @GetMapping
    @Operation(summary = "Lista todos los pagos", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<List<PaymentDTO>> getAll() {
        return ResponseEntity.ok(paymentService.getAll());
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Lista los pagos de un empleado", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<List<PaymentDTO>> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(paymentService.getByEmployee(employeeId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un pago por id", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<PaymentDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un pago no pagado", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
