package com.gestion.empleados.domains.payment.repository;

import com.gestion.empleados.domains.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {
    List<Payment> findAllByEmployeeId(Long employeeId);
    List<Payment> findAllByEmployeeIdOrderByPaymentDateDescPaidAtDescIdDesc(Long employeeId);
    List<Payment> findAllByEmployeeIdAndPaymentDateBetweenOrderByPaymentDateDescPaidAtDescIdDesc(Long employeeId, LocalDate from, LocalDate to);
    List<Payment> findAllByPaid(boolean paid);
    boolean existsByEmployeeIdAndPeriodStartAndPeriodEnd(Long employeeId, LocalDate periodStart, LocalDate periodEnd);
}
