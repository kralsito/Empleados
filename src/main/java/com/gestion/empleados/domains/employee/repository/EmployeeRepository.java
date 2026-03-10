package com.gestion.empleados.domains.employee.repository;

import com.gestion.empleados.domains.employee.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    List<Employee> findAllByActiveTrue();
    List<Employee> findAllByRoleIdAndActiveTrue(Long roleId);
}
