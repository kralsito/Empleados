package com.gestion.empleados.domains.role.service;

import com.gestion.empleados.domains.role.dto.request.RoleDTOin;
import com.gestion.empleados.domains.role.dto.response.RoleDTO;

import java.util.List;

public interface RoleService {
    RoleDTO create(RoleDTOin dto);

    RoleDTO getById(Long id);

    List<RoleDTO> getAll();
    RoleDTO update(Long id, RoleDTOin dto);
    void delete(Long id);
}
