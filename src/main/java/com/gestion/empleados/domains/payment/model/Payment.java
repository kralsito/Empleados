package com.gestion.empleados.domains.payment.model;

import com.gestion.empleados.domains.employee.model.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private LocalDate paymentDate;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    // Campos anteriores, mantenerlos por compatibilidad
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private BigDecimal totalHours;
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String paymentProof;

    private LocalDateTime paidAt;
    private boolean paid;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaymentAllocation> allocations;

    public enum PaymentType {
        COMPLETO, PARCIAL
    }

    public enum PaymentMethod {
        EFECTIVO, TRANSFERENCIA, COMBINADO
    }
}
