package com.gestion.empleados.domains.role.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
public class RoleDTOin {
    private String name;
    private BigDecimal salaryHour;
}
