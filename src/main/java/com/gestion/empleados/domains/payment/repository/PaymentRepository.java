package com.gestion.empleados.domains.payment.repository;

import com.gestion.empleados.domains.payment.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {
    List<Payment> findAllByEmployeeId(Long employeeId);
    List<Payment> findAllByUserId(Long userId);
    List<Payment> findAllByEmployeeIdAndUserIdOrderByPaymentDateDescPaidAtDescIdDesc(Long employeeId, Long userId);
    List<Payment> findAllByEmployeeIdAndUserIdAndPaymentDateBetweenOrderByPaymentDateDescPaidAtDescIdDesc(Long employeeId, Long userId, LocalDate from, LocalDate to);
    List<Payment> findAllByPaid(boolean paid);
    Optional<Payment> findByIdAndUserId(Long id, Long userId);
    boolean existsByEmployeeIdAndPeriodStartAndPeriodEnd(Long employeeId, LocalDate periodStart, LocalDate periodEnd);
    Page<Payment> findAllByEmployeeIdAndUserId(Long employeeId, Long userId, Pageable pageable);
    Page<Payment> findAllByEmployeeIdAndUserIdAndPaymentDateBetween(Long employeeId, Long userId, LocalDate from, LocalDate to, Pageable pageable);
}
