package com.gestion.empleados.domains.role.controller;

import com.gestion.empleados.domains.role.dto.request.RoleDTOin;
import com.gestion.empleados.domains.role.dto.response.RoleDTO;
import com.gestion.empleados.domains.role.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
@Tag(name = "Role", description = "Role Endpoints")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @Operation(summary = "Crea un rol", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<RoleDTO> create(@Valid @RequestBody RoleDTOin dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.create(dto));
    }

    @GetMapping
    @Operation(summary = "Lista todos los roles", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<List<RoleDTO>> getAll() {
        return ResponseEntity.ok(roleService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un rol por id", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<RoleDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un rol", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<RoleDTO> update(@PathVariable Long id, @Valid @RequestBody RoleDTOin dto) {
        return ResponseEntity.ok(roleService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un rol", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
