package com.gestion.empleados.domains.employee.service;

import com.gestion.empleados.domains.employee.dto.request.EmployeeDTOin;
import com.gestion.empleados.domains.employee.dto.response.EmployeeDTO;

public interface EmployeeService {
    EmployeeDTO create(EmployeeDTOin dto);
}
