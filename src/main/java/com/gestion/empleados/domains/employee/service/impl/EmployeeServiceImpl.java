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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, UserRepository userRepository, RoleRepository roleRepository) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public EmployeeDTO create(EmployeeDTOin dto){
        Long userId = AuthSupport.getUserId();
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new BadRequestException(UserError.USER_NOT_LOGIN);
        }
        Employee employee = EmployeeMapper.MAPPER.toEntity(dto);
        Role role = getRole(dto);
        employee.setRole(role);
        employee.setActive(true);
        employee = employeeRepository.save(employee);
        return EmployeeMapper.MAPPER.toDto(employee);
    }

    @Override
    public List<EmployeeDTO> getAll() {
        return employeeRepository.findAllByActiveTrue()
                .stream()
                .map(EmployeeMapper.MAPPER::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDTO getById(Long id){
        Employee employee = getEmployee(id);
        return EmployeeMapper.MAPPER.toDto(employee);
    }

    @Override
    @Transactional
    public EmployeeDTO update(Long id, EmployeeDTOin dto) {
        Employee employee = getEmployee(id);
        Role role = getRole(dto);
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

    @Override
    public List<EmployeeDTO> getByRole(Long roleId) {
        return employeeRepository.findAllByRoleIdAndActiveTrue(roleId)
                .stream()
                .map(EmployeeMapper.MAPPER::toDto)
                .collect(Collectors.toList());
    }

    private Employee getEmployee(Long id){
        Optional<Employee> employeeOptional = employeeRepository.findById(id);
        if(employeeOptional.isEmpty()){
            throw new BadRequestException(EmployeeError.EMPLOYEE_NOT_FOUND);
        }
        return employeeOptional.get();
    }


    private Role getRole(EmployeeDTOin dto){
        Optional<Role> roleOptional = roleRepository.findById(dto.getRoleId());
        if(roleOptional.isEmpty()){
            throw new BadRequestException(RoleError.ROLE_NOT_FOUND);
        }
        Role role = roleOptional.get();
        return role;
    }
}
