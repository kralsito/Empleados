package com.gestion.empleados.domains.payment.dto.response;

import com.gestion.empleados.domains.payment.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDetailDTO {
    private Long id;
    private Long employeeId;
    private LocalDate date;
    private BigDecimal amount;
    private Payment.PaymentType type;
    private LocalDateTime paidAt;
    private Payment.PaymentMethod paymentMethod;
    private String paymentProof;
    private BigDecimal totalWorkedHours;
    private List<AllocationDTO> assignedWorklogs;
}
