package org.basket.mapper;

import org.basket.model.CartItem;
import org.basket.request.CartMenuItemRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CartMenuItemMapper {

    CartMenuItemMapper INSTANCE = Mappers.getMapper(CartMenuItemMapper.class);

    CartItem cartMenuItemRequestToCartItem(CartMenuItemRequest request);

}
