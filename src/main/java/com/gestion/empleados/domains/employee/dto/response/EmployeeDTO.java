package com.gestion.empleados.domains.employee.dto.response;

import com.gestion.empleados.domains.role.dto.response.RoleDTO;
import lombok.Data;

@Data
public class EmployeeDTO {
    private Long id;
    private String name;
    private String lastName;
    private boolean active;
    private RoleDTO role;
}
