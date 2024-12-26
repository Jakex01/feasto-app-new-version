package org.restaurant.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.restaurant.model.OrderLocationEntity;
import org.restaurant.request.OrderLocationRequest;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);

    OrderLocationEntity locationRequestToEntity(OrderLocationRequest orderLocationRequest);
}
