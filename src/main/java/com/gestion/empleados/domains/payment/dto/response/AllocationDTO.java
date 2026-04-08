package com.gestion.empleados.domains.payment.dto.response;

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
public class AllocationDTO {
    private Long worklogId;
    private LocalDate date;
    private String description;
    private BigDecimal hours;
    private BigDecimal paidAmount;
}
