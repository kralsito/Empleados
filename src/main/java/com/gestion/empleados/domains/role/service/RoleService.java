package com.gestion.empleados.domains.role.service;

import com.gestion.empleados.domains.role.dto.request.RoleDTOin;
import com.gestion.empleados.domains.role.dto.response.RoleDTO;

public interface RoleService {
    RoleDTO create(RoleDTOin dto);
}
