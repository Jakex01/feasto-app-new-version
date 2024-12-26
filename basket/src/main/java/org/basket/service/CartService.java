package org.basket.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.basket.config.UrlConfig;
import org.basket.exceptions.CartItemNotFoundException;
import org.basket.exceptions.CartNotFoundException;
import org.basket.mapper.CartMenuItemMapper;
import org.basket.model.Cart;
import org.basket.model.CartItem;
import org.basket.repository.CartRepository;
import org.basket.request.CartRequest;
import org.basket.response.CartResponse;
import org.basket.util.JwtUtil;
import org.basket.util.WebClientService;
import org.basket.validators.ObjectsValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class CartService {

    private CartRepository cartRepository;
    private final WebClientService webClientService;
    private final UrlConfig urlConfig;
    private final ObjectsValidator<CartRequest> cartItemValidator;
    private static final String FAILED_TO_FETCH_USER_EMAIL = "Failed to fetch user email or email is empty";
    public List<CartResponse> getCartByUserId(String token) {
        String jwtToken = JwtUtil.extractToken(token);
        String userEmail = webClientService.fetchData(urlConfig.getSecurityService(), String.class, jwtToken).block();
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, FAILED_TO_FETCH_USER_EMAIL);
        }
        Cart cart = cartRepository.findById(userEmail).orElse(new Cart());
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            return List.of();
        }
        return cart.getItems().entrySet().stream()
                .map(entry -> {
                    Long menuItemId = entry.getKey();
                    CartItem item = entry.getValue();
                    return new CartResponse(
                            menuItemId,
                            item.getName(),
                            item.getCategory(),
                            item.getRestaurantId(),
                            item.getRestaurantName(),
                            item.getQuantity(),
                            item.getSize(),
                            item.getPrice(),
                            item.getAdditives()
                    );
                })
                .toList();
    }

    @Transactional
    public Cart addOrUpdateItem(CartRequest cartRequest, String token) {
        cartItemValidator.validate(cartRequest);
        String jwtToken = JwtUtil.extractToken(token);
        String userEmail = webClientService.fetchData(urlConfig.getSecurityService(), String.class, jwtToken).block();
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, FAILED_TO_FETCH_USER_EMAIL);
        }
        Cart cart = cartRepository.findById(userEmail).orElse(new Cart(userEmail, new HashMap<>()));
        CartItem cartItem = CartMenuItemMapper.INSTANCE.cartMenuItemRequestToCartItem(cartRequest.cartItem());
        cart.addItem(cartRequest.menuItemId(), cartItem);
        return cartRepository.save(cart);
    }
    @Transactional
    public Cart removeItem(Long menuItemId, String token) {
        String jwtToken = JwtUtil.extractToken(token);
        String userEmail = webClientService.fetchData(
                urlConfig.getSecurityService(), String.class, jwtToken
        ).block();

        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to fetch user email.");
        }

        log.info("Fetched userEmail: {}", userEmail);
        Iterable<Cart> carts = cartRepository.findAll();
        carts.forEach(cart -> log.info("Cart: " + cart));
        Cart cart = cartRepository.findById(userEmail)
                .orElseThrow(() -> new CartNotFoundException("Cart  not found with id: "+ userEmail));
        if (!cart.getItems().containsKey(menuItemId)) {
            throw new CartItemNotFoundException("Cart item with id: "+ menuItemId + "not found");
        }
        cart.removeItem(menuItemId);
        Cart updatedCart = cartRepository.save(cart);

        log.info("Updated cart saved for user: {}", userEmail);
        return updatedCart;
    }



    public void deleteCart( String token) {
        String jwtToken = JwtUtil.extractToken(token);
        String userEmail = webClientService.fetchData(urlConfig.getSecurityService(), String.class, jwtToken).block();
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, FAILED_TO_FETCH_USER_EMAIL);
        }
        cartRepository.deleteById(userEmail);
    }

    public Integer getNoCartItems(String token) {
        String jwtToken = JwtUtil.extractToken(token);
        String userEmail = webClientService.fetchData(urlConfig.getSecurityService(), String.class, jwtToken).block();
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, FAILED_TO_FETCH_USER_EMAIL);
        }
        Cart cart = cartRepository.findById(userEmail).orElse(new Cart(userEmail, new HashMap<>()));
        return cart.getItems().values().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

}