package org.basket.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.basket.model.Cart;
import org.basket.model.CartItem;
import org.basket.service.CartService;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private CartService cartService;

    @GetMapping
    public Cart getCart(
            @RequestHeader(value = "Authorization") String token
            ) {
        return cartService.getCartByUserId(token);
    }
    @PostMapping("/items")
    public Cart addOrUpdateItem(
                                @RequestBody CartItem cartItem,
                                @RequestParam @NotNull String productId,
                                @RequestHeader(value = "Authorization") String token
                                ) {
        return cartService.addOrUpdateItem(productId, cartItem, token);
    }
    @DeleteMapping("/items/{productId}")
    public Cart removeItem(
            @PathVariable @NotBlank String productId,
                           @RequestHeader(value = "Authorization") String token) {
        return cartService.removeItem( productId, token);
    }
    @DeleteMapping
    public void deleteCart(@RequestHeader(value = "Authorization") String token) {
        cartService.deleteCart(token);
    }
}
