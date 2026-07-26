package com.gestion.empleados.domains.payment.repository;

import com.gestion.empleados.domains.payment.model.PaymentAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentAllocationRepository extends JpaRepository<PaymentAllocation, Long> {
    List<PaymentAllocation> findAllByPaymentId(Long paymentId);
    List<PaymentAllocation> findAllByPaymentIdIn(List<Long> paymentIds);
}