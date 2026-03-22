package com.gestion.empleados.domains.worklog.dto.response;

import com.gestion.empleados.domains.employee.dto.response.EmployeeDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkLogDetailDTO {
    private Long id;
    private Long employeeId;
    private LocalDate date;
    private String dayOfWeek;
    private String description;
    private BigDecimal hours;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private BigDecimal remaining;
    private String status;
}