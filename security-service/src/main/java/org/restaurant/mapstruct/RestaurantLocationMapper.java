package org.restaurant.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.restaurant.model.LocationEntity;
import org.restaurant.request.LocationRequest;
import org.restaurant.response.LocationNamesResponse;
import org.restaurant.response.LocationResponse;

@Mapper(componentModel = "spring")
public interface RestaurantLocationMapper {

    RestaurantLocationMapper INSTANCE = Mappers.getMapper(RestaurantLocationMapper.class);

    LocationEntity locationRequestToLocationEntity(LocationRequest locationRequest);
    LocationResponse locationEntityToLocationResponse(LocationEntity locationEntity);
    LocationNamesResponse locationEntityToLocationNamesResponse(LocationEntity locationEntity);
}
