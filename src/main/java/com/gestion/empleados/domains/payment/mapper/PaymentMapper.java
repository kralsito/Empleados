package com.gestion.empleados.domains.payment.mapper;

import com.gestion.empleados.domains.payment.dto.request.PaymentDTOin;
import com.gestion.empleados.domains.payment.dto.response.PaymentDTO;
import com.gestion.empleados.domains.payment.model.Payment;
import com.gestion.empleados.shared.entity.EntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentMapper extends EntityMapper<PaymentDTO, Payment> {
    PaymentMapper MAPPER = Mappers.getMapper(PaymentMapper.class);
    Payment toEntity(PaymentDTOin dto);
}
