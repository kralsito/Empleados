package com.gestion.empleados.domains.user.mapper;

import com.gestion.empleados.domains.user.dto.request.UserDTOin;
import com.gestion.empleados.domains.user.dto.response.UserDTO;
import com.gestion.empleados.domains.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

    UserDTO toDto(User entity);

    @Mapping(target = "password", expression = "java(PasswordEncodedUtil.encode(dto.getPassword()))")
    User toEntity(UserDTOin dto);
}