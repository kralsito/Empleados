package com.gestion.empleados.domains.employee.controller;

import com.gestion.empleados.domains.employee.dto.request.EmployeeDTOin;
import com.gestion.empleados.domains.employee.dto.response.EmployeeDTO;
import com.gestion.empleados.domains.employee.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
@Tag(name = "Employee", description = "Employee Endpoints")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    @Operation(summary = "Crea un empleado", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<EmployeeDTO> create(@Valid @RequestBody EmployeeDTOin dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.create(dto));
    }

    @GetMapping
    @Operation(summary = "Obtiene todos los empleados", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<List<EmployeeDTO>> getAll() {
        return ResponseEntity.ok(employeeService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene los empleados por id", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<EmployeeDTO> getById(@PathVariable Long id) {
        EmployeeDTO response = employeeService.getById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un empleado", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<EmployeeDTO> update(@PathVariable Long id, @Valid @RequestBody EmployeeDTOin dto) {
        return ResponseEntity.ok(employeeService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un empleado", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/role/{roleId}")
    @Operation(summary = "Obtiene empleados por rol", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<List<EmployeeDTO>> getByRole(@PathVariable Long roleId) {
        return ResponseEntity.ok(employeeService.getByRole(roleId));
    }
}
