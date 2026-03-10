package com.gestion.empleados.domains.employee.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class EmployeeDTOin {
    @NotBlank
    private String name;
    @NotBlank
    private String lastName;
    @NotNull
    private Long roleId;
}
