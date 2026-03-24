package com.gestion.empleados.domains.payment.dto.response;

import com.gestion.empleados.domains.employee.dto.response.EmployeeDTO;
import com.gestion.empleados.domains.payment.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private EmployeeDTO employee;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private BigDecimal totalHours;
    private BigDecimal totalAmount;
    private Payment.PaymentMethod paymentMethod;
    private String paymentProof;
    private LocalDateTime paidAt;
    private boolean paid;
}
