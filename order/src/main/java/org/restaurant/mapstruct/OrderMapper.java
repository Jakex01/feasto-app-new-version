package org.restaurant.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.restaurant.model.OrderEntity;
import org.restaurant.response.OrderResponse;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderResponse orderEntityToResponse(OrderEntity entity);

}
