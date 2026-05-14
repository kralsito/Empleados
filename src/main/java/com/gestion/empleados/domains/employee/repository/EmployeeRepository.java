package com.gestion.empleados.domains.employee.repository;

import com.gestion.empleados.domains.employee.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    List<Employee> findAllByActiveTrueAndUserId(Long userId);
    Optional<Employee> findByIdAndUserId(Long id, Long userId);
    List<Employee> findAllByRoleIdAndActiveTrue(Long roleId);
    List<Employee> findAllByRoleIdAndActiveTrueAndUserId(Long roleId, Long userId);
}
