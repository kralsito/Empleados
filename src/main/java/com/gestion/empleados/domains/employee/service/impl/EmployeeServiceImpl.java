package com.gestion.empleados.domains.employee.service.impl;

import com.gestion.empleados.domains.employee.dto.request.EmployeeDTOin;
import com.gestion.empleados.domains.employee.dto.response.EmployeeDTO;
import com.gestion.empleados.domains.employee.error.EmployeeError;
import com.gestion.empleados.domains.employee.mapper.EmployeeMapper;
import com.gestion.empleados.domains.employee.model.Employee;
import com.gestion.empleados.domains.employee.repository.EmployeeRepository;
import com.gestion.empleados.domains.employee.service.EmployeeService;
import com.gestion.empleados.domains.role.dto.response.RoleDTO;
import com.gestion.empleados.domains.role.error.RoleError;
import com.gestion.empleados.domains.role.mapper.RoleMapper;
import com.gestion.empleados.domains.role.model.Role;
import com.gestion.empleados.domains.role.repository.RoleRepository;
import com.gestion.empleados.domains.user.error.UserError;
import com.gestion.empleados.domains.user.model.User;
import com.gestion.empleados.domains.user.repository.UserRepository;
import com.gestion.empleados.shared.config.AuthSupport;
import com.gestion.empleados.shared.exception.custom.BadRequestException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public EmployeeDTO create(EmployeeDTOin dto) {
        Long userId = AuthSupport.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(UserError.USER_NOT_LOGIN));
        Role role = roleRepository.findByIdAndUserId(dto.getRoleId(), userId)
                .orElseThrow(() -> new BadRequestException(RoleError.ROLE_NOT_FOUND));

        Employee employee = EmployeeMapper.MAPPER.toEntity(dto);
        employee.setRole(role);
        employee.setUser(user);
        employee.setActive(true);

        return EmployeeMapper.MAPPER.toDto(employeeRepository.save(employee));
    }

    @Override
    public List<EmployeeDTO> getAll() {
        Long userId = AuthSupport.getUserId();
        return employeeRepository.findAllByActiveTrueAndUserId(userId)
                .stream()
                .map(EmployeeMapper.MAPPER::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDTO getById(Long id) {
        return EmployeeMapper.MAPPER.toDto(getEmployee(id));
    }

    @Override
    public List<EmployeeDTO> getByRole(Long roleId) {
        Long userId = AuthSupport.getUserId();
        return employeeRepository.findAllByRoleIdAndActiveTrueAndUserId(roleId, userId)
                .stream()
                .map(EmployeeMapper.MAPPER::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EmployeeDTO update(Long id, EmployeeDTOin dto) {
        Long userId = AuthSupport.getUserId();
        Employee employee = getEmployee(id);
        Role role = roleRepository.findByIdAndUserId(dto.getRoleId(), userId)
                .orElseThrow(() -> new BadRequestException(RoleError.ROLE_NOT_FOUND));

        employee.setName(dto.getName());
        employee.setLastName(dto.getLastName());
        employee.setRole(role);

        return EmployeeMapper.MAPPER.toDto(employeeRepository.save(employee));
    }

    @Override
    public void delete(Long id) {
        Employee employee = getEmployee(id);
        employee.setActive(false);
        employeeRepository.save(employee);
    }

    private Employee getEmployee(Long id) {
        Long userId = AuthSupport.getUserId();
        return employeeRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BadRequestException(EmployeeError.EMPLOYEE_NOT_FOUND));
    }
}
