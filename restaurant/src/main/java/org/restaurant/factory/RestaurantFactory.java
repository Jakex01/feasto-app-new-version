package org.restaurant.factory;

import org.restaurant.mapstruct.dto.LocationMapper;
import org.restaurant.mapstruct.dto.MenuItemMapper;
import org.restaurant.mapstruct.dto.RestaurantMapper;
import org.restaurant.model.LocationEntity;
import org.restaurant.model.MenuItemEntity;
import org.restaurant.model.RestaurantEntity;
import org.restaurant.request.CreateRestaurantRequestDuplicate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RestaurantFactory {

    public RestaurantEntity createRestaurant(CreateRestaurantRequestDuplicate createRestaurant, String userEmail) {
        RestaurantEntity restaurantEntity = RestaurantMapper.INSTANCE
                .restaurantRequestToRestaurantEntity(createRestaurant.restaurantInfo());

        LocationEntity locationEntity = LocationMapper.INSTANCE
                .locationRequestToLocationEntity(createRestaurant.restaurantLocation());
        locationEntity.setRestaurant(restaurantEntity);

        List<MenuItemEntity> menuItemEntities = createRestaurant.restaurantMenuItems().stream()
                .map(MenuItemMapper.INSTANCE::menuItemRequestToMenuItemEntity)
                .toList();

        restaurantEntity.setLocations(List.of(locationEntity));
        restaurantEntity.setMenuItems(menuItemEntities);
        restaurantEntity.setManagerEmails(List.of(userEmail));

        return restaurantEntity;
    }
}
