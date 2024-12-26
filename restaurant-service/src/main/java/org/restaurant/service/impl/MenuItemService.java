package org.restaurant.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.restaurant.config.UrlConfig;
import org.restaurant.exceptions.AccessDeniedException;
import org.restaurant.exceptions.MenuItemNotFoundException;
import org.restaurant.exceptions.UserNotFoundException;
import org.restaurant.mapstruct.dto.MapStructMapper;
import org.restaurant.mapstruct.dto.MenuItemMapper;
import org.restaurant.model.MenuItemEntity;
import org.restaurant.model.RestaurantEntity;
import org.restaurant.repository.MenuItemRepository;
import org.restaurant.repository.RestaurantRepository;
import org.restaurant.request.PostMenuItemRequest;
import org.restaurant.response.CustomMenuItemResponse;
import org.restaurant.response.MenuItemIdAndNameResponse;
import org.restaurant.util.JwtUtil;
import org.restaurant.util.UserDetailsClient;
import org.restaurant.validators.MenuItemValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemValidator menuItemValidator;
    private final UserDetailsClient userDetailsClient;
    private final UrlConfig urlConfig;

    public ResponseEntity<CustomMenuItemResponse> getMenuItemEntityById(Long menuItemId) {

        return menuItemRepository
                .findById(menuItemId)
                .map(MapStructMapper.INSTANCE::menuItemEntityToCustomMenuItemResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() ->
                        new EntityNotFoundException("Menu item not found with id: " + menuItemId));
    }

    @Transactional
    public ResponseEntity<Void> updateMenuItemById(PostMenuItemRequest postMenuItemRequest, Long menuItemId) {
        menuItemValidator.validateRequest(postMenuItemRequest);
        MenuItemEntity menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new MenuItemNotFoundException("Menu item with id " + menuItemId + " not found"));
        MenuItemMapper.INSTANCE.updateMenuItemFromRequest(postMenuItemRequest, menuItem);
        return ResponseEntity.accepted().build();
    }

    @Transactional
    public ResponseEntity<Void> deleteMenuItemById(Long menuItemId) {
        menuItemRepository.deleteById(menuItemId);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<List<MenuItemIdAndNameResponse>> getMenuItemsByRestaurantId(Long restaurantId, String token) {
        String userEmail = userDetailsClient
                .fetchData(urlConfig.getUserService(), String.class, JwtUtil.extractToken(token))
                .block();

        if (userEmail == null) {
            throw new UserNotFoundException("User not found.");
        }
        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with id: " + restaurantId));
        if (!restaurant.getManagerEmails().contains(userEmail)) {
            throw new AccessDeniedException("User does not have permission to access this restaurant's menu items.");
        }
        List<MenuItemIdAndNameResponse> nameResponses = restaurant.getMenuItems()
                .stream()
                .map(menuItem -> new MenuItemIdAndNameResponse(
                        menuItem.getMenuItemId(),
                        menuItem.getName()
                ))
                .toList();
        return ResponseEntity.ok(nameResponses);
    }
}
