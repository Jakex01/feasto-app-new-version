package org.restaurant.mapstruct.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.restaurant.model.MenuItemEntity;
import org.restaurant.model.SizesWithPricesEntity;
import org.restaurant.request.PostMenuItemRequest;
import org.restaurant.request.SizesWithPrices;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MenuItemMapper {
    MenuItemMapper INSTANCE = Mappers.getMapper(MenuItemMapper.class);

    @Mapping(source = "sizesWithPrices", target = "sizesWithPrices", qualifiedByName = "mapSizesWithPrices")
    MenuItemEntity menuItemRequestToMenuItemEntity(PostMenuItemRequest request);

    @Named("mapSizesWithPrices")
    List<SizesWithPricesEntity> mapSizesWithPrices(List<SizesWithPrices> sizesWithPrices);
}
