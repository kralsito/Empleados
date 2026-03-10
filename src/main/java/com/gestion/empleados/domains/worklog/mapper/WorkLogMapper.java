package com.gestion.empleados.domains.worklog.mapper;

import com.gestion.empleados.domains.worklog.dto.request.WorkLogDTOin;
import com.gestion.empleados.domains.worklog.dto.response.WorkLogDTO;
import com.gestion.empleados.domains.worklog.model.WorkLog;
import com.gestion.empleados.shared.entity.EntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WorkLogMapper extends EntityMapper<WorkLogDTO, WorkLog> {
    WorkLogMapper MAPPER = Mappers.getMapper(WorkLogMapper.class);
    WorkLog toEntity(WorkLogDTOin dto);
}
