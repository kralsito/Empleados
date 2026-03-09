package com.gestion.empleados.domains.role.mapper;

import com.gestion.empleados.domains.role.dto.request.RoleDTOin;
import com.gestion.empleados.domains.role.dto.response.RoleDTO;
import com.gestion.empleados.domains.role.model.Role;
import com.gestion.empleados.shared.entity.EntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper extends EntityMapper<RoleDTO, Role> {
    RoleMapper MAPPER = Mappers.getMapper(RoleMapper.class);
    Role toEntity(RoleDTOin dto);
}
