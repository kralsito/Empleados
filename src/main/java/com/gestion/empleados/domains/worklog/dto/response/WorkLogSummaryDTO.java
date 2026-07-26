package com.gestion.empleados.domains.worklog.dto.response;

import java.math.BigDecimal;

public record WorkLogSummaryDTO(
        long totalCount,
        long paidCount,
        long pendingCount,
        BigDecimal totalBilled,
        BigDecimal totalPaid,
        BigDecimal pendingTotal
) {}