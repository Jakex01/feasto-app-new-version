package org.restaurant.mapstruct.dto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.restaurant.model.RestaurantEntity;
import org.restaurant.request.CreateRestaurantRequest;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {

    RestaurantMapper INSTANCE = Mappers.getMapper(RestaurantMapper.class);

    RestaurantEntity restaurantRequestToRestaurantEntity(CreateRestaurantRequest request);
}
