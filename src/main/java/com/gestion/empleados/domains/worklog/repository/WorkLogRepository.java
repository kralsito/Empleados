package com.gestion.empleados.domains.worklog.repository;

import com.gestion.empleados.domains.worklog.model.WorkLog;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface WorkLogRepository extends JpaRepository<WorkLog, Long>, JpaSpecificationExecutor<WorkLog> {
    List<WorkLog> findAllByEmployeeId(Long employeeId);
    List<WorkLog> findAllByEmployeeIdAndDateBetween(Long employeeId, LocalDate from, LocalDate to);
    List<WorkLog> findAllByDateBetween(LocalDate from, LocalDate to);
    boolean existsByEmployeeIdAndDate(Long employeeId, LocalDate date);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM WorkLog w WHERE w.employee.id = :employeeId AND w.paidAmount < w.totalDay ORDER BY w.date ASC")
    List<WorkLog> findPendingByEmployeeIdOrderByDateAsc(@Param("employeeId") Long employeeId);
}
