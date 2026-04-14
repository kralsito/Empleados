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
    List<WorkLog> findAllByEmployeeIdAndUserId(Long employeeId, Long userId);
    List<WorkLog> findAllByEmployeeIdAndDateBetweenAndUserId(Long employeeId, LocalDate from, LocalDate to, Long userId);
    List<WorkLog> findAllByDateBetweenAndUserId(LocalDate from, LocalDate to, Long userId);
    boolean existsByEmployeeIdAndDateAndUserId(Long employeeId, LocalDate date, Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM WorkLog w WHERE w.employee.id = :employeeId AND w.user.id = :userId AND w.paidAmount < w.totalDay ORDER BY w.date ASC")
    List<WorkLog> findPendingByEmployeeIdAndUserIdOrderByDateAsc(@Param("employeeId") Long employeeId, @Param("userId") Long userId);
}
