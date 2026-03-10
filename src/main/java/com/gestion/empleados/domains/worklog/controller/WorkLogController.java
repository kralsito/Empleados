package com.gestion.empleados.domains.worklog.controller;

import com.gestion.empleados.domains.worklog.dto.request.WorkLogDTOin;
import com.gestion.empleados.domains.worklog.dto.response.WorkLogDTO;
import com.gestion.empleados.domains.worklog.service.WorkLogService;
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
@RequestMapping("/work-logs")
@RequiredArgsConstructor
@Tag(name = "WorkLog", description = "WorkLog Endpoints")
public class WorkLogController {

    private final WorkLogService workLogService;

    @PostMapping
    @Operation(summary = "Carga horas de un empleado", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<WorkLogDTO> create(@Valid @RequestBody WorkLogDTOin dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workLogService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza horas de un empleado", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<WorkLogDTO> update(@PathVariable Long id, @Valid @RequestBody WorkLogDTOin dto) {
        return ResponseEntity.ok(workLogService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un registro", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workLogService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Obtiene registros por empleado", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<List<WorkLogDTO>> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(workLogService.getByEmployee(employeeId));
    }

    @GetMapping("/employee/{employeeId}/period")
    @Operation(summary = "Obtiene registros por empleado y período", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<List<WorkLogDTO>> getByEmployeeAndPeriod(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(workLogService.getByEmployeeAndPeriod(employeeId, from, to));
    }

    @GetMapping("/period")
    @Operation(summary = "Obtiene registros de todos los empleados por período", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<List<WorkLogDTO>> getByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(workLogService.getByPeriod(from, to));
    }
}