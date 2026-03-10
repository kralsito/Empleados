package com.gestion.empleados.domains.role.service.impl;

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

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public RoleServiceImpl(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
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

    private Role getRole(Long id){
        Optional<Role> roleOptional = roleRepository.findById(id);
        if(roleOptional.isEmpty()){
            throw new BadRequestException(RoleError.ROLE_NOT_FOUND);
        }
        return roleOptional.get();
    }
}
