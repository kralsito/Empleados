package com.gestion.empleados.domains.payment.service;

import com.gestion.empleados.domains.payment.dto.request.PaymentDTOin;
import com.gestion.empleados.domains.payment.dto.response.PaymentDTO;

import java.util.List;

public interface PaymentService {
    PaymentDTO create(PaymentDTOin dto);
    PaymentDTO update(Long id, PaymentDTOin dto);
    PaymentDTO pay(Long id);
    List<PaymentDTO> getAll();
    List<PaymentDTO> getByEmployee(Long employeeId);
    PaymentDTO getById(Long id);
    void delete(Long id);
}
