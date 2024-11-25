package org.restaurant.mapstruct.dto;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.restaurant.model.LocationEntity;
import org.restaurant.request.PostLocationRequest;
import org.restaurant.request.update.UpdateLocationRequest;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);

    LocationEntity locationRequestToLocationEntity(PostLocationRequest request);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    LocationEntity updateRequestToEntity(UpdateLocationRequest request, @MappingTarget LocationEntity restaurant);
}
