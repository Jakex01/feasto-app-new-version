package org.restaurant.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.restaurant.response.CustomMenuItemResponse;
import org.restaurant.service.MenuItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
@RequestMapping("/api/restaurant/menu-item")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @GetMapping("/{id}")
    public ResponseEntity<CustomMenuItemResponse> getMenuItemEntityById(@PathVariable("id") @Valid Long menuItemId){
        return menuItemService.getMenuItemEntityById(menuItemId);
    }
}
