package org.basket.controller;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.basket.model.Cart;
import org.basket.request.CartRequest;
import org.basket.response.CartResponse;
import org.basket.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/basket")
public class CartController {

    private CartService cartService;

    @GetMapping
    public List<CartResponse> getCart(
            @RequestHeader(value = "Authorization") String token
            ) {
        return cartService.getCartByUserId(token);
    }
    @PostMapping("/items")
    public Cart addOrUpdateItem(
                                @RequestBody CartRequest cartItem,
                                @RequestHeader(value = "Authorization") String token
                                ) {
        return cartService.addOrUpdateItem(cartItem, token);
    }
    @DeleteMapping("/items/{productId}")
    public Cart removeItem(
            @PathVariable @NotNull Long productId,
                           @RequestHeader(value = "Authorization") String token) {
        return cartService.removeItem( productId, token);
    }
    @DeleteMapping
    public void deleteCart(@RequestHeader(value = "Authorization") String token) {
        cartService.deleteCart(token);
    }

    @GetMapping("/no")
    public Integer getNoCartItems(@RequestHeader(value = "Authorization") String token) {
        return cartService.getNoCartItems(token);
    }
}
