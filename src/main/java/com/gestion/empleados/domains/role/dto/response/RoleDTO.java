package com.gestion.empleados.domains.role.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RoleDTO {
    private Long id;
    private String name;
    private BigDecimal salaryHour;
}
