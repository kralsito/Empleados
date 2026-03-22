package com.gestion.empleados.domains.payment.dto.response;

import com.gestion.empleados.domains.worklog.dto.response.WorkLogDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplyPaymentDTO {
    private PaymentDetailDTO payment;
    private List<WorkLogDetailDTO> updatedWorklogs;
}
