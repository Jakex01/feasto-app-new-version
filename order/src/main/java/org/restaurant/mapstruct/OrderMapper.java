package org.restaurant.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.restaurant.model.OrderEntity;
import org.restaurant.model.event.StatisticsEvent;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    StatisticsEvent orderEntityToStatisticsEvent(OrderEntity orderEntity);
}
