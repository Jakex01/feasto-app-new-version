package org.restaurant.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.restaurant.model.MenuItemOrder;
import org.restaurant.response.MenuItemResponse;

@Mapper(componentModel = "spring")
public interface MenuItemMapper {
    MenuItemMapper INSTANCE = Mappers.getMapper(MenuItemMapper.class);

    MenuItemResponse menuEntityToResponse(MenuItemOrder menuItemOrder);
}
