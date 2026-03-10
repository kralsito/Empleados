package com.gestion.empleados.domains.worklog.dto.response;

import com.gestion.empleados.domains.employee.dto.response.EmployeeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkLogDTO {
    private Long id;
    private EmployeeDTO employee;
    private LocalDate date;
    private String dayOfWeek;
    private BigDecimal hoursWorked;
    private BigDecimal salaryHourSnapshot;
    private BigDecimal totalDay;
}