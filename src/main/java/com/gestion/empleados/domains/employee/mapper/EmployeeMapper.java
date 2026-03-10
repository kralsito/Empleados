package com.gestion.empleados.domains.employee.mapper;

import com.gestion.empleados.domains.employee.dto.request.EmployeeDTOin;
import com.gestion.empleados.domains.employee.dto.response.EmployeeDTO;
import com.gestion.empleados.domains.employee.model.Employee;
import com.gestion.empleados.shared.entity.EntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeMapper extends EntityMapper<EmployeeDTO, Employee> {
    EmployeeMapper MAPPER = Mappers.getMapper(EmployeeMapper.class);
    Employee toEntity(EmployeeDTOin dto);
}
