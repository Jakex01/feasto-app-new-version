package org.restaurant.mapstruct.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.restaurant.model.LocationEntity;
import org.restaurant.request.PostLocationRequest;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);

    LocationEntity locationRequestToLocationEntity(PostLocationRequest request);
}
