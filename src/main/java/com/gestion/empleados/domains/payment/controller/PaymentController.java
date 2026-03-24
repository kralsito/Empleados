package com.gestion.empleados.domains.payment.controller;

import com.gestion.empleados.domains.payment.dto.request.ApplyPaymentDTOin;
import com.gestion.empleados.domains.payment.dto.request.PaymentDTOin;
import com.gestion.empleados.domains.payment.dto.response.ApplyPaymentDTO;
import com.gestion.empleados.domains.payment.dto.response.PaymentDTO;
import com.gestion.empleados.domains.payment.dto.response.PaymentDetailDTO;
import com.gestion.empleados.domains.payment.service.PaymentApplyService;
import com.gestion.empleados.domains.payment.service.PaymentService;
import com.gestion.empleados.domains.worklog.dto.response.WorkLogDetailDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment Endpoints")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentApplyService paymentApplyService;


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


    @PostMapping("/apply")
    @Operation(summary = "Aplica un pago con distribución FIFO", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<ApplyPaymentDTO> apply(@Valid @RequestBody ApplyPaymentDTOin dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentApplyService.apply(dto));
    }

    @GetMapping("/employee/{employeeId}/worklogs")
    @Operation(summary = "Obtiene worklogs con estado de pago por empleado", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<List<WorkLogDetailDTO>> getWorklogsForEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(paymentApplyService.getWorklogsForEmployee(employeeId));
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Obtiene historial de pagos por empleado", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<List<PaymentDetailDTO>> getPaymentsForEmployee(
            @PathVariable Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(paymentApplyService.getPaymentsForEmployee(employeeId, from, to));
    }
}
