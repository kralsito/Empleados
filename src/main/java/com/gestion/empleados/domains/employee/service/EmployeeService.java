package com.gestion.empleados.domains.employee.service;

import com.gestion.empleados.domains.employee.dto.request.EmployeeDTOin;
import com.gestion.empleados.domains.employee.dto.response.EmployeeDTO;

import java.util.List;

public interface EmployeeService {
    EmployeeDTO create(EmployeeDTOin dto);

    List<EmployeeDTO> getAll();

    EmployeeDTO getById(Long id);

    EmployeeDTO update(Long id, EmployeeDTOin dto);

    void delete(Long id);

    List<EmployeeDTO> getByRole(Long roleId);
}
