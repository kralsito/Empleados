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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public RoleDTO create(RoleDTOin dto) {
        Long userId = AuthSupport.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(UserError.USER_NOT_LOGIN));
        Role role = RoleMapper.MAPPER.toEntity(dto);
        role.setUser(user);
        return RoleMapper.MAPPER.toDto(roleRepository.save(role));
    }

    @Override
    public RoleDTO getById(Long id) {
        return RoleMapper.MAPPER.toDto(getRole(id));
    }

    @Override
    public List<RoleDTO> getAll() {
        Long userId = AuthSupport.getUserId();
        return roleRepository.findAllByUserId(userId)
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

    private Role getRole(Long id) {
        Long userId = AuthSupport.getUserId();
        return roleRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BadRequestException(RoleError.ROLE_NOT_FOUND));
    }
}
