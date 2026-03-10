package com.gestion.empleados.domains.worklog.model;

import com.gestion.empleados.domains.employee.model.Employee;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "work_logs")
public class WorkLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private LocalDate date;
    private BigDecimal hoursWorked;
    private BigDecimal salaryHourSnapshot;
    private BigDecimal totalDay;
}
