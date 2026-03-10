package com.gestion.empleados.domains.worklog.repository;

import com.gestion.empleados.domains.worklog.model.WorkLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface WorkLogRepository extends JpaRepository<WorkLog, Long>, JpaSpecificationExecutor<WorkLog> {
    List<WorkLog> findAllByEmployeeId(Long employeeId);
    List<WorkLog> findAllByEmployeeIdAndDateBetween(Long employeeId, LocalDate from, LocalDate to);
    List<WorkLog> findAllByDateBetween(LocalDate from, LocalDate to);
    boolean existsByEmployeeIdAndDate(Long employeeId, LocalDate date);
}
