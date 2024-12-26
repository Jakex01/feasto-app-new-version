package org.restaurant.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.restaurant.request.PostMenuItemRequest;
import org.restaurant.response.CustomMenuItemResponse;
import org.restaurant.response.MenuItemIdAndNameResponse;
import org.restaurant.service.impl.MenuItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/restaurant/menu-item")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @GetMapping("/select/{id}")
    public ResponseEntity<CustomMenuItemResponse> getMenuItemEntityById(@PathVariable("id") @Valid Long menuItemId) {
        return menuItemService.getMenuItemEntityById(menuItemId);
    }
    @GetMapping("/{restaurantId}")
    public ResponseEntity<List<MenuItemIdAndNameResponse>> getMenuItemsByRestaurantId(@PathVariable Long restaurantId,
                                                                                      @RequestHeader(value = "Authorization") String token) {
        return menuItemService.getMenuItemsByRestaurantId(restaurantId, token);
    }
    @PatchMapping("/{menuItemId}")
    public ResponseEntity<Void> updateMenuItemEntityById(@RequestBody PostMenuItemRequest postMenuItemRequest, @PathVariable Long menuItemId) {
        return menuItemService.updateMenuItemById(postMenuItemRequest, menuItemId);
    }
    @DeleteMapping("/{menuItemId}")
    public ResponseEntity<Void> deleteMenuItemEntityById(@PathVariable Long menuItemId) {
        return menuItemService.deleteMenuItemById(menuItemId);
    }
}
