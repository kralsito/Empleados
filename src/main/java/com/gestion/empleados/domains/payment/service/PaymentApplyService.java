package com.gestion.empleados.domains.payment.service;

import com.gestion.empleados.domains.payment.dto.request.ApplyPaymentDTOin;
import com.gestion.empleados.domains.payment.dto.response.ApplyPaymentDTO;
import com.gestion.empleados.domains.payment.dto.response.PaymentDetailDTO;
import com.gestion.empleados.domains.worklog.dto.response.WorkLogDetailDTO;
import com.gestion.empleados.domains.worklog.dto.response.WorkLogSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface PaymentApplyService {
    ApplyPaymentDTO apply(ApplyPaymentDTOin request) throws IOException;
    Page<WorkLogDetailDTO> getWorklogsForEmployee(Long employeeId, Boolean paid, Pageable pageable);
    WorkLogSummaryDTO getWorklogsSummary(Long employeeId);
    Page<PaymentDetailDTO> getPaymentsForEmployee(Long employeeId, LocalDate from, LocalDate to, Pageable pageable);
    List<WorkLogDetailDTO> getWorklogsByRange(Long employeeId, LocalDate from, LocalDate to);

}
