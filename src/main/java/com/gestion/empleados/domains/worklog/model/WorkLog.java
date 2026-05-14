package com.gestion.empleados.domains.worklog.model;

import com.gestion.empleados.domains.employee.model.Employee;
import com.gestion.empleados.domains.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "work_logs")
public class WorkLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDate date;
    private String description;
    private BigDecimal hoursWorked;
    private BigDecimal salaryHourSnapshot;
    private BigDecimal totalDay;

    @Builder.Default
    @Column(nullable = false)
    private BigDecimal paidAmount = BigDecimal.ZERO;
}
