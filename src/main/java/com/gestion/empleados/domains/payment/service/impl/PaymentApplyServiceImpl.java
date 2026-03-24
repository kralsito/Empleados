package com.gestion.empleados.domains.payment.service.impl;

import com.gestion.empleados.domains.employee.error.EmployeeError;
import com.gestion.empleados.domains.employee.model.Employee;
import com.gestion.empleados.domains.employee.repository.EmployeeRepository;
import com.gestion.empleados.domains.payment.dto.request.ApplyPaymentDTOin;
import com.gestion.empleados.domains.payment.dto.response.AllocationDTO;
import com.gestion.empleados.domains.payment.dto.response.ApplyPaymentDTO;
import com.gestion.empleados.domains.payment.dto.response.PaymentDetailDTO;
import com.gestion.empleados.domains.payment.error.PaymentError;
import com.gestion.empleados.domains.payment.model.Payment;
import com.gestion.empleados.domains.payment.model.PaymentAllocation;
import com.gestion.empleados.domains.payment.repository.PaymentAllocationRepository;
import com.gestion.empleados.domains.payment.repository.PaymentRepository;
import com.gestion.empleados.domains.payment.service.PaymentApplyService;
import com.gestion.empleados.domains.worklog.dto.response.WorkLogDetailDTO;
import com.gestion.empleados.domains.worklog.model.WorkLog;
import com.gestion.empleados.domains.worklog.repository.WorkLogRepository;
import com.gestion.empleados.shared.exception.custom.BadRequestException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentApplyServiceImpl implements PaymentApplyService {

    private final WorkLogRepository workLogRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentAllocationRepository allocationRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public ApplyPaymentDTO apply(ApplyPaymentDTOin request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new BadRequestException(EmployeeError.EMPLOYEE_NOT_FOUND));

        List<WorkLog> pendingWorklogs = workLogRepository
                .findPendingByEmployeeIdOrderByDateAsc(request.getEmployeeId());

        if (pendingWorklogs.isEmpty()) {
            throw new BadRequestException(PaymentError.NO_WORKLOGS);
        }

        BigDecimal totalPending = pendingWorklogs.stream()
                .map(w -> w.getTotalDay().subtract(w.getPaidAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(PaymentError.INVALID_AMOUNT);
        }

        if (request.getAmount().compareTo(totalPending) > 0) {
            throw new BadRequestException(PaymentError.AMOUNT_EXCEEDS_PENDING);
        }

        BigDecimal remainingToPay = request.getAmount();
        List<PaymentAllocation> allocations = new ArrayList<>();
        List<WorkLog> updatedWorklogs = new ArrayList<>();

        for (WorkLog worklog : pendingWorklogs) {
            if (remainingToPay.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal pending = worklog.getTotalDay().subtract(worklog.getPaidAmount());
            BigDecimal payNow = remainingToPay.min(pending);

            if (payNow.compareTo(BigDecimal.ZERO) > 0) {
                worklog.setPaidAmount(worklog.getPaidAmount().add(payNow));
                remainingToPay = remainingToPay.subtract(payNow);
                updatedWorklogs.add(worklog);
                allocations.add(PaymentAllocation.builder()
                        .workLog(worklog)
                        .paidAmount(payNow)
                        .build());
            }
        }

        workLogRepository.saveAll(updatedWorklogs);

        Payment.PaymentType type = request.getComplete()
                ? Payment.PaymentType.COMPLETO
                : Payment.PaymentType.PARCIAL;

        Payment payment = Payment.builder()
                .employee(employee)
                .paymentDate(request.getDate())
                .amount(request.getAmount())
                .paymentType(type)
                .paymentMethod(request.getPaymentMethod())
                .paymentProof(normalizeProof(request.getPaymentProof()))
                .paid(true)
                .paidAt(LocalDateTime.now())
                .build();

        payment = paymentRepository.save(payment);

        Payment finalPayment = payment;
        allocations.forEach(a -> a.setPayment(finalPayment));
        allocationRepository.saveAll(allocations);

        return ApplyPaymentDTO.builder()
                .payment(toPaymentDetailDTO(finalPayment, allocations, request.getEmployeeId()))
                .updatedWorklogs(updatedWorklogs.stream()
                        .map(this::toWorkLogDetailDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkLogDetailDTO> getWorklogsForEmployee(Long employeeId) {
        return workLogRepository.findAllByEmployeeId(employeeId)
                .stream()
                .map(this::toWorkLogDetailDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDetailDTO> getPaymentsForEmployee(Long employeeId, LocalDate from, LocalDate to) {
        List<Payment> payments = (from != null && to != null)
                ? paymentRepository.findAllByEmployeeIdAndPaymentDateBetweenOrderByPaymentDateDescPaidAtDescIdDesc(employeeId, from, to)
                : paymentRepository.findAllByEmployeeIdOrderByPaymentDateDescPaidAtDescIdDesc(employeeId);

        return payments
                .stream()
                .filter(p -> p.getPaymentType() != null)
                .sorted(
                        Comparator.comparing(Payment::getPaymentDate, Comparator.nullsLast(Comparator.reverseOrder()))
                                .thenComparing(Payment::getPaidAt, Comparator.nullsLast(Comparator.reverseOrder()))
                                .thenComparing(Payment::getId, Comparator.nullsLast(Comparator.reverseOrder()))
                )
                .map(p -> {
                    List<PaymentAllocation> allocations = allocationRepository.findAllByPaymentId(p.getId());
                    return toPaymentDetailDTO(p, allocations, employeeId);
                })
                .collect(Collectors.toList());
    }

    private WorkLogDetailDTO toWorkLogDetailDTO(WorkLog w) {
        BigDecimal remaining = w.getTotalDay().subtract(w.getPaidAmount());
        String dayOfWeek = w.getDate()
                .getDayOfWeek()
                .getDisplayName(TextStyle.FULL, new Locale("es", "AR"));

        return WorkLogDetailDTO.builder()
                .id(w.getId())
                .employeeId(w.getEmployee().getId())
                .date(w.getDate())
                .dayOfWeek(dayOfWeek)
                .description(w.getDescription())
                .hours(w.getHoursWorked())
                .amount(w.getTotalDay())
                .paidAmount(w.getPaidAmount())
                .remaining(remaining)
                .status(resolveStatus(w.getPaidAmount(), w.getTotalDay()))
                .build();
    }

    private PaymentDetailDTO toPaymentDetailDTO(Payment p, List<PaymentAllocation> allocations, Long employeeId) {
        List<AllocationDTO> allocationDTOs = allocations.stream()
                .map(a -> {
                    WorkLog workLog = a.getWorkLog();
                    BigDecimal allocatedHours = calculateAllocatedHours(workLog, a.getPaidAmount());

                    return AllocationDTO.builder()
                            .worklogId(workLog.getId())
                            .date(workLog.getDate())
                            .description(workLog.getDescription())
                            .hours(allocatedHours)
                            .paidAmount(a.getPaidAmount())
                            .build();
                })
                .collect(Collectors.toList());

        BigDecimal totalWorkedHours = allocations.stream()
                .map(a -> calculateAllocatedHours(a.getWorkLog(), a.getPaidAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return PaymentDetailDTO.builder()
                .id(p.getId())
                .employeeId(employeeId)
                .date(p.getPaymentDate())
                .amount(p.getAmount())
                .type(p.getPaymentType())
                .paidAt(p.getPaidAt())
                .paymentMethod(p.getPaymentMethod())
                .paymentProof(p.getPaymentProof())
                .totalWorkedHours(totalWorkedHours)
                .assignedWorklogs(allocationDTOs)
                .build();
    }

    private String resolveStatus(BigDecimal paidAmount, BigDecimal totalDay) {
        if (paidAmount.compareTo(BigDecimal.ZERO) == 0) return "PENDIENTE";
        if (paidAmount.compareTo(totalDay) >= 0) return "PAGADO";
        return "PARCIAL";
    }

    private String normalizeProof(String proof) {
        if (proof == null) return null;

        String normalized = proof.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private BigDecimal calculateAllocatedHours(WorkLog workLog, BigDecimal paidAmount) {
        if (workLog.getTotalDay() == null || workLog.getTotalDay().compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return workLog.getHoursWorked()
                .multiply(paidAmount)
                .divide(workLog.getTotalDay(), 4, RoundingMode.HALF_UP);
    }
}
