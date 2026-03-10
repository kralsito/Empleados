package com.gestion.empleados.domains.payment.service.impl;

import com.gestion.empleados.domains.employee.error.EmployeeError;
import com.gestion.empleados.domains.employee.model.Employee;
import com.gestion.empleados.domains.employee.repository.EmployeeRepository;
import com.gestion.empleados.domains.payment.dto.request.PaymentDTOin;
import com.gestion.empleados.domains.payment.dto.response.PaymentDTO;
import com.gestion.empleados.domains.payment.error.PaymentError;
import com.gestion.empleados.domains.payment.mapper.PaymentMapper;
import com.gestion.empleados.domains.payment.model.Payment;
import com.gestion.empleados.domains.payment.repository.PaymentRepository;
import com.gestion.empleados.domains.payment.service.PaymentService;
import com.gestion.empleados.domains.worklog.model.WorkLog;
import com.gestion.empleados.domains.worklog.repository.WorkLogRepository;
import com.gestion.empleados.shared.exception.custom.BadRequestException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final EmployeeRepository employeeRepository;
    private final WorkLogRepository workLogRepository;

    @Override
    @Transactional
    public PaymentDTO create(PaymentDTOin dto) {
        Employee employee = getEmployee(dto.getEmployeeId());

        if (paymentRepository.existsByEmployeeIdAndPeriodStartAndPeriodEnd(
                dto.getEmployeeId(), dto.getPeriodStart(), dto.getPeriodEnd())) {
            throw new BadRequestException(PaymentError.ALREADY_EXISTS);
        }

        List<WorkLog> workLogs = workLogRepository
                .findAllByEmployeeIdAndDateBetween(dto.getEmployeeId(), dto.getPeriodStart(), dto.getPeriodEnd());

        if (workLogs.isEmpty()) {
            throw new BadRequestException(PaymentError.NO_WORKLOGS);
        }

        BigDecimal totalHours = workLogs.stream()
                .map(WorkLog::getHoursWorked)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAmount = workLogs.stream()
                .map(WorkLog::getTotalDay)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Payment payment = Payment.builder()
                .employee(employee)
                .periodStart(dto.getPeriodStart())
                .periodEnd(dto.getPeriodEnd())
                .totalHours(totalHours)
                .totalAmount(totalAmount)
                .paymentMethod(dto.getPaymentMethod())
                .paid(false)
                .build();

        return PaymentMapper.MAPPER.toDto(paymentRepository.save(payment));
    }

    @Override
    @Transactional
    public PaymentDTO update(Long id, PaymentDTOin dto) {
        Payment payment = getPayment(id);

        if (payment.isPaid()) {
            throw new BadRequestException(PaymentError.ALREADY_PAID);
        }

        List<WorkLog> workLogs = workLogRepository
                .findAllByEmployeeIdAndDateBetween(dto.getEmployeeId(), dto.getPeriodStart(), dto.getPeriodEnd());

        BigDecimal totalHours = workLogs.stream()
                .map(WorkLog::getHoursWorked)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAmount = workLogs.stream()
                .map(WorkLog::getTotalDay)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        payment.setPeriodStart(dto.getPeriodStart());
        payment.setPeriodEnd(dto.getPeriodEnd());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setTotalHours(totalHours);
        payment.setTotalAmount(totalAmount);

        return PaymentMapper.MAPPER.toDto(paymentRepository.save(payment));
    }

    @Override
    @Transactional
    public PaymentDTO pay(Long id) {
        Payment payment = getPayment(id);

        if (payment.isPaid()) {
            throw new BadRequestException(PaymentError.ALREADY_PAID);
        }

        payment.setPaid(true);
        payment.setPaidAt(LocalDateTime.now());

        return PaymentMapper.MAPPER.toDto(paymentRepository.save(payment));
    }

    @Override
    public List<PaymentDTO> getAll() {
        return paymentRepository.findAll()
                .stream()
                .map(PaymentMapper.MAPPER::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDTO> getByEmployee(Long employeeId) {
        return paymentRepository.findAllByEmployeeId(employeeId)
                .stream()
                .map(PaymentMapper.MAPPER::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentDTO getById(Long id) {
        return PaymentMapper.MAPPER.toDto(getPayment(id));
    }

    @Override
    public void delete(Long id) {
        Payment payment = getPayment(id);
        if (payment.isPaid()) {
            throw new BadRequestException(PaymentError.ALREADY_PAID);
        }
        paymentRepository.delete(payment);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Employee getEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(EmployeeError.EMPLOYEE_NOT_FOUND));
    }

    private Payment getPayment(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(PaymentError.NOT_FOUND));
    }
}
