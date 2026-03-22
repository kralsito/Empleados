package com.gestion.empleados.domains.payment.dto.response;

import com.gestion.empleados.domains.payment.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private List<AllocationDTO> assignedWorklogs;
}
