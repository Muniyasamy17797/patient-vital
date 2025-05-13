package com.vital.app.domain.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.vital.app.domain.dto.VitalRequest;
import com.vital.app.domain.dto.VitalSignResponse;
import com.vital.app.domain.model.VitalSign;

@Mapper(componentModel = "spring")
public interface VitalMapper {


    @Mapping(target = "patient", ignore = true)  // Ignore patientId during mapping
    @Mapping(target = "documentedBy", ignore = true)
        // Ignore documentedBy during mapping
    VitalSignResponse toResponse(VitalSign vitalSign);

    VitalSign toEntity(VitalRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(VitalRequest dto, @MappingTarget VitalSign entity);
    
}
