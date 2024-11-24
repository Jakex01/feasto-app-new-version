package org.restaurant.mapstruct.dto;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.restaurant.model.MenuItemEntity;
import org.restaurant.model.SizesWithPricesEntity;
import org.restaurant.request.PostMenuItemRequest;
import org.restaurant.request.SizesWithPrices;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MenuItemMapper {
    MenuItemMapper INSTANCE = Mappers.getMapper(MenuItemMapper.class);

    MenuItemEntity menuItemRequestToMenuItemEntity(PostMenuItemRequest request);

    //    @Named("mapSizesWithPrices")
    List<SizesWithPricesEntity> mapSizesWithPrices(List<SizesWithPrices> sizesWithPrices);
    SizesWithPricesEntity map(SizesWithPrices value);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateMenuItemFromRequest(PostMenuItemRequest request, @MappingTarget MenuItemEntity menuItemEntity);
}

