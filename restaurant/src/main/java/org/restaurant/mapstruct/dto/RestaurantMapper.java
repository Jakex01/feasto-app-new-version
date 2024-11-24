package org.restaurant.mapstruct.dto;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.restaurant.model.RestaurantEntity;
import org.restaurant.request.CreateRestaurantRequest;
import org.restaurant.request.update.UpdateRestaurantRequest;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {

    RestaurantMapper INSTANCE = Mappers.getMapper(RestaurantMapper.class);

    RestaurantEntity restaurantRequestToRestaurantEntity(CreateRestaurantRequest request);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRestaurantFromRequest(UpdateRestaurantRequest request, @MappingTarget RestaurantEntity restaurant);
}
