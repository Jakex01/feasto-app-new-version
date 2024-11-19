package org.basket.service;

import lombok.AllArgsConstructor;
import org.basket.model.Cart;
import org.basket.model.CartItem;
import org.basket.repository.CartRepository;
import org.basket.util.JwtUtil;
import org.basket.util.WebClientService;
import org.basket.validators.ObjectsValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;

@Service
@AllArgsConstructor
public class CartService {

    private CartRepository cartRepository;
    private final WebClientService webClientService;
    private final ObjectsValidator<CartItem> cartItemValidator;
    private static final String USER_URL = "http://localhost:8762/api/auth/user";
    public Cart getCartByUserId(String token) {
        String jwtToken = JwtUtil.extractToken(token);
        String userEmail = webClientService.fetchData(USER_URL, String.class, jwtToken).block();
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to fetch user email or email is empty");
        }
        return cartRepository.findById(userEmail).orElse(new Cart(userEmail, new HashMap<>()));
    }

    public Cart addOrUpdateItem( String productId, CartItem cartItem, String token) {
        cartItemValidator.validate(cartItem);
        String jwtToken = JwtUtil.extractToken(token);
        String userEmail = webClientService.fetchData(USER_URL, String.class, jwtToken).block();
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to fetch user email or email is empty");
        }
        Cart cart = getCartByUserId(userEmail);
        cart.addItem(productId, cartItem);
        return cartRepository.save(cart);
    }

    public Cart removeItem(String productId, String token) {
        String jwtToken = JwtUtil.extractToken(token);
        String userEmail = webClientService.fetchData(USER_URL, String.class, jwtToken).block();
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to fetch user email or email is empty");
        }
        Cart cart = getCartByUserId(userEmail);
        cart.removeItem(productId);
        return cartRepository.save(cart);
    }

    public void deleteCart( String token) {
        String jwtToken = JwtUtil.extractToken(token);
        String userEmail = webClientService.fetchData(USER_URL, String.class, jwtToken).block();
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to fetch user email or email is empty");
        }
        cartRepository.deleteById(userEmail);
    }
}