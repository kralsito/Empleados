package com.gestion.empleados.domains.role.service.impl;

import com.gestion.empleados.domains.employee.repository.EmployeeRepository;
import com.gestion.empleados.domains.role.dto.request.RoleDTOin;
import com.gestion.empleados.domains.role.dto.response.RoleDTO;
import com.gestion.empleados.domains.role.error.RoleError;
import com.gestion.empleados.domains.role.mapper.RoleMapper;
import com.gestion.empleados.domains.role.model.Role;
import com.gestion.empleados.domains.role.repository.RoleRepository;
import com.gestion.empleados.domains.role.service.RoleService;
import com.gestion.empleados.domains.user.error.UserError;
import com.gestion.empleados.domains.user.model.User;
import com.gestion.empleados.domains.user.repository.UserRepository;
import com.gestion.empleados.shared.config.AuthSupport;
import com.gestion.empleados.shared.exception.custom.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    public RoleServiceImpl(RoleRepository roleRepository, UserRepository userRepository, EmployeeRepository employeeRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public RoleDTO create(RoleDTOin dto){
        Long userId = AuthSupport.getUserId();
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new BadRequestException(UserError.USER_NOT_LOGIN);
        }
        Role role = RoleMapper.MAPPER.toEntity(dto);
        role = roleRepository.save(role);
        return RoleMapper.MAPPER.toDto(role);
    }

    @Override
    public RoleDTO getById(Long id){
        Role role = getRole(id);
        return RoleMapper.MAPPER.toDto(role);
    }

    @Override
    public List<RoleDTO> getAll() {
        return roleRepository.findAll()
                .stream()
                .map(RoleMapper.MAPPER::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RoleDTO update(Long id, RoleDTOin dto) {
        Role role = getRole(id);
        role.setName(dto.getName());
        role.setSalaryHour(dto.getSalaryHour());
        return RoleMapper.MAPPER.toDto(roleRepository.save(role));
    }

    @Override
    public void delete(Long id) {
        Role role = getRole(id);
        if (!employeeRepository.findAllByRoleIdAndActiveTrue(id).isEmpty()) {
            throw new BadRequestException(RoleError.HAS_EMPLOYEES);
        }
        roleRepository.delete(role);
    }

    private Role getRole(Long id){
        Optional<Role> roleOptional = roleRepository.findById(id);
        if(roleOptional.isEmpty()){
            throw new BadRequestException(RoleError.ROLE_NOT_FOUND);
        }
        return roleOptional.get();
    }
}
