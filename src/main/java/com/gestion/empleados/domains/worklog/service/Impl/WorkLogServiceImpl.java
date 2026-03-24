package com.gestion.empleados.domains.worklog.service.Impl;

import com.gestion.empleados.domains.employee.error.EmployeeError;
import com.gestion.empleados.domains.employee.model.Employee;
import com.gestion.empleados.domains.employee.repository.EmployeeRepository;
import com.gestion.empleados.domains.worklog.dto.request.WorkLogDTOin;
import com.gestion.empleados.domains.worklog.dto.response.WorkLogDTO;
import com.gestion.empleados.domains.worklog.error.WorkLogError;
import com.gestion.empleados.domains.worklog.mapper.WorkLogMapper;
import com.gestion.empleados.domains.worklog.model.WorkLog;
import com.gestion.empleados.domains.worklog.repository.WorkLogRepository;
import com.gestion.empleados.domains.worklog.service.WorkLogService;
import com.gestion.empleados.shared.exception.custom.BadRequestException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkLogServiceImpl implements WorkLogService {

    private final WorkLogRepository workLogRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public WorkLogDTO create(WorkLogDTOin dto) {
        Employee employee = getEmployee(dto.getEmployeeId());

        if (workLogRepository.existsByEmployeeIdAndDate(dto.getEmployeeId(), dto.getDate())) {
            throw new BadRequestException(WorkLogError.ALREADY_EXISTS);
        }

        WorkLog workLog = WorkLogMapper.MAPPER.toEntity(dto);
        workLog.setEmployee(employee);
        workLog.setDescription(normalizeDescription(dto.getDescription()));
        workLog.setSalaryHourSnapshot(employee.getRole().getSalaryHour());
        workLog.setTotalDay(dto.getHoursWorked().multiply(employee.getRole().getSalaryHour()));

        return toDto(workLogRepository.save(workLog));
    }

    @Override
    @Transactional
    public WorkLogDTO update(Long id, WorkLogDTOin dto) {
        WorkLog workLog = getWorkLog(id);

        workLog.setDescription(normalizeDescription(dto.getDescription()));
        workLog.setHoursWorked(dto.getHoursWorked());
        workLog.setTotalDay(dto.getHoursWorked().multiply(workLog.getSalaryHourSnapshot()));

        return toDto(workLogRepository.save(workLog));
    }

    @Override
    public void delete(Long id) {
        WorkLog workLog = getWorkLog(id);
        workLogRepository.delete(workLog);
    }

    @Override
    public List<WorkLogDTO> getByEmployee(Long employeeId) {
        return workLogRepository.findAllByEmployeeId(employeeId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkLogDTO> getByEmployeeAndPeriod(Long employeeId, LocalDate from, LocalDate to) {
        return workLogRepository.findAllByEmployeeIdAndDateBetween(employeeId, from, to)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkLogDTO> getByPeriod(LocalDate from, LocalDate to) {
        return workLogRepository.findAllByDateBetween(from, to)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


    private Employee getEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(EmployeeError.EMPLOYEE_NOT_FOUND));
    }

    private WorkLog getWorkLog(Long id) {
        return workLogRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(WorkLogError.WORKLOG_NOT_FOUND));
    }

    private WorkLogDTO toDto(WorkLog workLog) {
        WorkLogDTO dto = WorkLogMapper.MAPPER.toDto(workLog);
        dto.setDayOfWeek(workLog.getDate()
                .getDayOfWeek()
                .getDisplayName(TextStyle.FULL, new Locale("es", "AR")));
        return dto;
    }

    private String normalizeDescription(String description) {
        if (description == null) return null;

        String normalized = description.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
