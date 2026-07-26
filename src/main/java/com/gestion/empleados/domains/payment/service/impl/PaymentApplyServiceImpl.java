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
import com.gestion.empleados.domains.user.error.UserError;
import com.gestion.empleados.domains.user.model.User;
import com.gestion.empleados.domains.user.repository.UserRepository;
import com.gestion.empleados.domains.worklog.dto.response.WorkLogDetailDTO;
import com.gestion.empleados.domains.worklog.dto.response.WorkLogSummaryDTO;
import com.gestion.empleados.domains.worklog.model.WorkLog;
import com.gestion.empleados.domains.worklog.repository.WorkLogRepository;
import com.gestion.empleados.shared.config.AuthSupport;
import com.gestion.empleados.shared.exception.custom.BadRequestException;
import com.gestion.empleados.shared.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentApplyServiceImpl implements PaymentApplyService {

    private final WorkLogRepository workLogRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentAllocationRepository allocationRepository;
    private final EmployeeRepository employeeRepository;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ApplyPaymentDTO apply(ApplyPaymentDTOin request) throws IOException {
        Long userId = AuthSupport.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(UserError.USER_NOT_LOGIN));
        Employee employee = employeeRepository.findByIdAndUserId(request.getEmployeeId(), userId)
                .orElseThrow(() -> new BadRequestException(EmployeeError.EMPLOYEE_NOT_FOUND));

        List<WorkLog> pendingWorklogs = workLogRepository
                .findPendingByEmployeeIdAndUserIdOrderByDateAsc(request.getEmployeeId(), userId);

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

        String proofFilename = fileStorageService.store(request.getPaymentProof());

        Payment payment = Payment.builder()
                .employee(employee)
                .user(user)
                .paymentDate(request.getDate())
                .amount(request.getAmount())
                .paymentType(type)
                .paymentMethod(request.getPaymentMethod())
                .paymentProof(proofFilename)
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
    public Page<WorkLogDetailDTO> getWorklogsForEmployee(Long employeeId, Boolean paid, Pageable pageable) {
        Long userId = AuthSupport.getUserId();

        Page<WorkLog> page;
        if (Boolean.TRUE.equals(paid)) {
            page = workLogRepository.findPaidPageByEmployeeIdAndUserId(employeeId, userId, pageable);
        } else if (Boolean.FALSE.equals(paid)) {
            page = workLogRepository.findPendingPageByEmployeeIdAndUserId(employeeId, userId, pageable);
        } else {
            page = workLogRepository.findPageByEmployeeIdAndUserId(employeeId, userId, pageable);
        }

        return page.map(this::toWorkLogDetailDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkLogSummaryDTO getWorklogsSummary(Long employeeId) {
        Long userId = AuthSupport.getUserId();
        return workLogRepository.getSummaryByEmployeeIdAndUserId(employeeId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDetailDTO> getPaymentsForEmployee(Long employeeId, LocalDate from, LocalDate to, Pageable pageable) {
        Long userId = AuthSupport.getUserId();

        Page<Payment> page = (from != null && to != null)
                ? paymentRepository.findAllByEmployeeIdAndUserIdAndPaymentDateBetween(employeeId, userId, from, to, pageable)
                : paymentRepository.findAllByEmployeeIdAndUserId(employeeId, userId, pageable);

        List<Long> paymentIds = page.getContent().stream()
                .map(Payment::getId)
                .collect(Collectors.toList());

        Map<Long, List<PaymentAllocation>> allocationsByPayment = allocationRepository
                .findAllByPaymentIdIn(paymentIds)
                .stream()
                .collect(Collectors.groupingBy(a -> a.getPayment().getId()));

        return page.map(p -> toPaymentDetailDTO(
                p,
                allocationsByPayment.getOrDefault(p.getId(), List.of()),
                employeeId
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkLogDetailDTO> getWorklogsByRange(Long employeeId, LocalDate from, LocalDate to) {
        Long userId = AuthSupport.getUserId();
        return workLogRepository.findAllByEmployeeIdAndDateBetweenAndUserId(employeeId, from, to, userId)
                .stream()
                .map(this::toWorkLogDetailDTO)
                .collect(Collectors.toList());
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

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

    private BigDecimal calculateAllocatedHours(WorkLog workLog, BigDecimal paidAmount) {
        if (workLog.getTotalDay() == null || workLog.getTotalDay().compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return workLog.getHoursWorked()
                .multiply(paidAmount)
                .divide(workLog.getTotalDay(), 4, RoundingMode.HALF_UP);
    }
}