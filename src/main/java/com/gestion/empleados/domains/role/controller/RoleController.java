package com.gestion.empleados.domains.role.controller;

import com.gestion.empleados.domains.role.dto.request.RoleDTOin;
import com.gestion.empleados.domains.role.dto.response.RoleDTO;
import com.gestion.empleados.domains.role.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/role")
@Tag(name = "Role", description = "Role Endpoints")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    @Operation(summary = "Crea un rol", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<RoleDTO> create(@RequestParam String name, @RequestParam BigDecimal salaryHour) {
        RoleDTOin dto = new RoleDTOin(name, salaryHour);
        RoleDTO response = roleService.create(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene los roles por id", security = { @SecurityRequirement(name = "bearer-jwt") })
    public ResponseEntity<RoleDTO> getById(@PathVariable Long id) {
        RoleDTO response = roleService.getById(id);
        return ResponseEntity.ok(response);
    }
}
