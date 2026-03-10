package com.gestion.empleados.domains.payment.dto.request;

import com.gestion.empleados.domains.payment.model.Payment;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTOin {
    @NotNull
    private Long employeeId;
    @NotNull
    private LocalDate periodStart;
    @NotNull
    private LocalDate periodEnd;
    @NotNull
    private Payment.PaymentMethod paymentMethod;
}
