package com.gestion.empleados.domains.worklog.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkLogDTOin {
    @NotNull
    private Long employeeId;
    @NotNull
    private LocalDate date;
    @NotNull
    @DecimalMin("0.5")
    @DecimalMax("24.0")
    private BigDecimal hoursWorked;
}
