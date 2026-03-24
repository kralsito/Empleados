package com.gestion.empleados.domains.payment.service;

import com.gestion.empleados.domains.payment.dto.request.ApplyPaymentDTOin;
import com.gestion.empleados.domains.payment.dto.response.ApplyPaymentDTO;
import com.gestion.empleados.domains.payment.dto.response.PaymentDetailDTO;
import com.gestion.empleados.domains.worklog.dto.response.WorkLogDetailDTO;

import java.time.LocalDate;
import java.util.List;

public interface PaymentApplyService {
    ApplyPaymentDTO apply(ApplyPaymentDTOin request);
    List<WorkLogDetailDTO> getWorklogsForEmployee(Long employeeId);
    List<PaymentDetailDTO> getPaymentsForEmployee(Long employeeId, LocalDate from, LocalDate to);
}
