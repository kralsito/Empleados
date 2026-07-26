package com.gestion.empleados.domains.payment.controller;

import com.gestion.empleados.domains.payment.dto.request.ApplyPaymentDTOin;
import com.gestion.empleados.domains.payment.dto.request.PaymentDTOin;
import com.gestion.empleados.domains.payment.dto.response.ApplyPaymentDTO;
import com.gestion.empleados.domains.payment.dto.response.PaymentDTO;
import com.gestion.empleados.domains.payment.dto.response.PaymentDetailDTO;
import com.gestion.empleados.domains.payment.service.PaymentApplyService;
import com.gestion.empleados.domains.payment.service.PaymentService;
import com.gestion.empleados.domains.worklog.dto.response.WorkLogDetailDTO;
import com.gestion.empleados.domains.worklog.dto.response.WorkLogSummaryDTO;
import com.gestion.empleados.shared.storage.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment Endpoints")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentApplyService paymentApplyService;
    private final FileStorageService fileStorageService;

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

    // ─── Apply (multipart/form-data) ─────────────────────────────────────────

    @PostMapping(value = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Aplica un pago con distribución FIFO", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<ApplyPaymentDTO> apply(
            @RequestParam Long employeeId,
            @RequestParam String date,
            @RequestParam java.math.BigDecimal amount,
            @RequestParam Boolean complete,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) MultipartFile paymentProof
    ) throws IOException {
        ApplyPaymentDTOin dto = new ApplyPaymentDTOin();
        dto.setEmployeeId(employeeId);
        dto.setDate(LocalDate.parse(date));
        dto.setAmount(amount);
        dto.setComplete(complete);
        dto.setPaymentMethod(com.gestion.empleados.domains.payment.model.Payment.PaymentMethod.valueOf(paymentMethod));
        dto.setPaymentProof(paymentProof);

        return ResponseEntity.status(HttpStatus.CREATED).body(paymentApplyService.apply(dto));
    }

    // ─── Serve proof file ─────────────────────────────────────────────────────

    @GetMapping("/{id}/proof")
    @Operation(summary = "Descarga el comprobante de un pago", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<Resource> getProof(@PathVariable Long id) throws MalformedURLException {
        PaymentDTO payment = paymentService.getById(id);

        if (payment.getPaymentProof() == null) {
            return ResponseEntity.notFound().build();
        }

        Path filePath = fileStorageService.resolve(payment.getPaymentProof());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String contentType;
        try {
            contentType = Files.probeContentType(filePath);
        } catch (IOException e) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        if (contentType == null) contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + payment.getPaymentProof() + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    // ─── Employee queries ─────────────────────────────────────────────────────

    @GetMapping("/employee/{employeeId}/worklogs")
    @Operation(summary = "Obtiene worklogs paginados con estado de pago por empleado", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<Page<WorkLogDetailDTO>> getWorklogsForEmployee(
            @PathVariable Long employeeId,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));
        return ResponseEntity.ok(paymentApplyService.getWorklogsForEmployee(employeeId, paid, pageable));
    }

    @GetMapping("/employee/{employeeId}/worklogs/summary")
    @Operation(summary = "Obtiene el resumen de montos y cantidades de worklogs por empleado", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<WorkLogSummaryDTO> getWorklogsSummary(@PathVariable Long employeeId) {
        return ResponseEntity.ok(paymentApplyService.getWorklogsSummary(employeeId));
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Obtiene historial de pagos paginado por empleado", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<Page<PaymentDetailDTO>> getPaymentsForEmployee(
            @PathVariable Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Sort sort = Sort.by(Sort.Direction.DESC, "paymentDate")
                .and(Sort.by(Sort.Direction.DESC, "paidAt"))
                .and(Sort.by(Sort.Direction.DESC, "id"));
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(paymentApplyService.getPaymentsForEmployee(employeeId, from, to, pageable));
    }

    @GetMapping("/employee/{employeeId}/worklogs/range")
    @Operation(summary = "Obtiene worklogs sin paginar dentro de un rango de fechas", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<List<WorkLogDetailDTO>> getWorklogsByRange(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(paymentApplyService.getWorklogsByRange(employeeId, from, to));
    }
}
