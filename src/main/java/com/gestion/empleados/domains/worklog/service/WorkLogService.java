package com.gestion.empleados.domains.worklog.service;

import com.gestion.empleados.domains.worklog.dto.request.WorkLogDTOin;
import com.gestion.empleados.domains.worklog.dto.response.WorkLogDTO;

import java.time.LocalDate;
import java.util.List;

public interface WorkLogService {
    WorkLogDTO create(WorkLogDTOin dto);
    WorkLogDTO update(Long id, WorkLogDTOin dto);
    void delete(Long id);
    List<WorkLogDTO> getByEmployee(Long employeeId);
    List<WorkLogDTO> getByEmployeeAndPeriod(Long employeeId, LocalDate from, LocalDate to);
    List<WorkLogDTO> getByPeriod(LocalDate from, LocalDate to);
}
