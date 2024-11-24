package org.restaurant.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.restaurant.exceptions.MenuItemNotFoundException;
import org.restaurant.mapstruct.dto.MapStructMapper;
import org.restaurant.mapstruct.dto.MenuItemMapper;
import org.restaurant.model.MenuItemEntity;
import org.restaurant.repository.MenuItemRepository;
import org.restaurant.request.PostMenuItemRequest;
import org.restaurant.response.CustomMenuItemResponse;
import org.restaurant.validators.MenuItemValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuItemValidator menuItemValidator;

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
}
