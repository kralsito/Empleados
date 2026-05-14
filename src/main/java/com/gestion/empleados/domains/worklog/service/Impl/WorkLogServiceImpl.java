package com.gestion.empleados.domains.worklog.service.Impl;

import com.gestion.empleados.domains.employee.error.EmployeeError;
import com.gestion.empleados.domains.employee.model.Employee;
import com.gestion.empleados.domains.employee.repository.EmployeeRepository;
import com.gestion.empleados.domains.user.error.UserError;
import com.gestion.empleados.domains.user.model.User;
import com.gestion.empleados.domains.user.repository.UserRepository;
import com.gestion.empleados.domains.worklog.dto.request.WorkLogDTOin;
import com.gestion.empleados.domains.worklog.dto.response.WorkLogDTO;
import com.gestion.empleados.domains.worklog.error.WorkLogError;
import com.gestion.empleados.domains.worklog.mapper.WorkLogMapper;
import com.gestion.empleados.domains.worklog.model.WorkLog;
import com.gestion.empleados.domains.worklog.repository.WorkLogRepository;
import com.gestion.empleados.domains.worklog.service.WorkLogService;
import com.gestion.empleados.shared.config.AuthSupport;
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
    private final UserRepository userRepository;

    @Override
    @Transactional
    public WorkLogDTO create(WorkLogDTOin dto) {
        Long userId = AuthSupport.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(UserError.USER_NOT_LOGIN));
        Employee employee = getEmployee(dto.getEmployeeId(), userId);

        if (workLogRepository.existsByEmployeeIdAndDateAndUserId(dto.getEmployeeId(), dto.getDate(), userId)) {
            throw new BadRequestException(WorkLogError.ALREADY_EXISTS);
        }

        WorkLog workLog = WorkLogMapper.MAPPER.toEntity(dto);
        workLog.setEmployee(employee);
        workLog.setUser(user);
        workLog.setSalaryHourSnapshot(employee.getRole().getSalaryHour());
        workLog.setTotalDay(dto.getHoursWorked().multiply(employee.getRole().getSalaryHour()));

        return toDto(workLogRepository.save(workLog));
    }

    @Override
    @Transactional
    public WorkLogDTO update(Long id, WorkLogDTOin dto) {
        Long userId = AuthSupport.getUserId();
        WorkLog workLog = getWorkLog(id, userId);

        workLog.setHoursWorked(dto.getHoursWorked());
        workLog.setTotalDay(dto.getHoursWorked().multiply(workLog.getSalaryHourSnapshot()));
        workLog.setDescription(dto.getDescription());

        return toDto(workLogRepository.save(workLog));
    }

    @Override
    public void delete(Long id) {
        Long userId = AuthSupport.getUserId();
        WorkLog workLog = getWorkLog(id, userId);
        workLogRepository.delete(workLog);
    }

    @Override
    public List<WorkLogDTO> getByEmployee(Long employeeId) {
        Long userId = AuthSupport.getUserId();
        return workLogRepository.findAllByEmployeeIdAndUserId(employeeId, userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkLogDTO> getByEmployeeAndPeriod(Long employeeId, LocalDate from, LocalDate to) {
        Long userId = AuthSupport.getUserId();
        return workLogRepository.findAllByEmployeeIdAndDateBetweenAndUserId(employeeId, from, to, userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkLogDTO> getByPeriod(LocalDate from, LocalDate to) {
        Long userId = AuthSupport.getUserId();
        return workLogRepository.findAllByDateBetweenAndUserId(from, to, userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Employee getEmployee(Long employeeId, Long userId) {
        return employeeRepository.findByIdAndUserId(employeeId, userId)
                .orElseThrow(() -> new BadRequestException(EmployeeError.EMPLOYEE_NOT_FOUND));
    }

    private WorkLog getWorkLog(Long id, Long userId) {
        return workLogRepository.findById(id)
                .filter(w -> w.getUser().getId().equals(userId))
                .orElseThrow(() -> new BadRequestException(WorkLogError.WORKLOG_NOT_FOUND));
    }

    private WorkLogDTO toDto(WorkLog w) {
        String dayOfWeek = w.getDate()
                .getDayOfWeek()
                .getDisplayName(TextStyle.FULL, new Locale("es", "AR"));

        return WorkLogDTO.builder()
                .id(w.getId())
                .employeeId(w.getEmployee().getId())
                .nombreEmpleado(w.getEmployee().getName() + " " + w.getEmployee().getLastName())
                .date(w.getDate())
                .dayOfWeek(dayOfWeek)
                .hoursWorked(w.getHoursWorked())
                .salaryHourSnapshot(w.getSalaryHourSnapshot())
                .totalDay(w.getTotalDay())
                .observacion(w.getDescription())
                .build();
    }
}
