package org.basket.service;

import org.basket.exceptions.BadCredentialException;
import org.basket.model.Cart;
import org.basket.model.CartItem;
import org.basket.repository.CartRepository;
import org.basket.util.JwtUtil;
import org.basket.util.WebClientService;
import org.basket.validators.ObjectsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {
    @Mock
    private CartRepository cartRepository;

    @Mock
    private WebClientService webClientService;

    @Mock
    private ObjectsValidator<CartItem> cartItemValidator;

    @InjectMocks
    private CartService cartService;

    private static final String FAILED_TO_FETCH_USER_EMAIL = "Failed to fetch user email or email is empty";

    private static final String USER_URL = "http://localhost:8762/api/auth/user";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCartByUserId_ShouldReturnCart_WhenUserExists() {
        // Arrange
        String token = "Bearer validToken123";
        String jwtToken = "validToken123";
        String userEmail = "test@example.com";
        Cart expectedCart = new Cart(userEmail, new HashMap<>());

        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.extractToken(token)).thenReturn(jwtToken);

            when(webClientService.fetchData(USER_URL, String.class, jwtToken)).thenReturn(Mono.just(userEmail));
            when(cartRepository.findById(userEmail)).thenReturn(Optional.of(expectedCart));

            // Act
            Cart cart = cartService.getCartByUserId(token);

            // Assert
            assertNotNull(cart);
            assertEquals(userEmail, cart.getUserEmail());
            verify(cartRepository, times(1)).findById(userEmail);
        }
    }

    @Test
    void getCartByUserId_ShouldReturnNewCart_WhenUserDoesNotExist() {
        // Arrange
        String token = "Bearer validToken123";
        String jwtToken = "validToken123";
        String userEmail = "test@example.com";

        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.extractToken(token)).thenReturn(jwtToken);

            when(webClientService.fetchData(USER_URL, String.class, jwtToken)).thenReturn(Mono.just(userEmail));
            when(cartRepository.findById(userEmail)).thenReturn(Optional.empty());

            // Act
            Cart cart = cartService.getCartByUserId(token);

            // Assert
            assertNotNull(cart);
            assertEquals(userEmail, cart.getUserEmail());
            assertTrue(cart.getItems().isEmpty());
            verify(cartRepository, times(1)).findById(userEmail);
        }
    }

    @Test
    void getCartByUserId_ShouldThrowException_WhenTokenIsInvalid() {
        // Arrange
        String token = null;

        // Act & Assert
        Exception exception = assertThrows(BadCredentialException.class, () ->
                cartService.getCartByUserId(token)
        );

        // Assert
        assertEquals("Invalid token: Token cannot be null", exception.getMessage());
    }

    @Test
    void getCartByUserId_ShouldThrowException_WhenUserEmailIsEmpty() {
        // Arrange
        String token = "Bearer validToken123";
        String jwtToken = "validToken123";

        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.extractToken(token)).thenReturn(jwtToken);

            when(webClientService.fetchData(USER_URL, String.class, jwtToken)).thenReturn(Mono.just(""));

            // Act & Assert
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                    cartService.getCartByUserId(token)
            );

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            assertEquals("Failed to fetch user email or email is empty", exception.getReason());

        }
    }

    @Test
    void addOrUpdateItem_ShouldThrowException_WhenUserEmailIsEmpty() {
        // Arrange
        String token = "Bearer validToken123";
        String jwtToken = "validToken123";
        String productId = "product1";
        CartItem cartItem = new CartItem("Item 1", 2, "Large", 100, Map.of());

        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.extractToken(token)).thenReturn(jwtToken);

            when(webClientService.fetchData(USER_URL, String.class, jwtToken)).thenReturn(Mono.just(""));

            // Act & Assert
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                    cartService.addOrUpdateItem(productId, cartItem, token)
            );

            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            assertEquals(FAILED_TO_FETCH_USER_EMAIL, exception.getReason());
        }
    }
    @Test
    void deleteCart_ShouldDeleteCart_WhenInputsAreValid() {
        // Arrange
        String token = "Bearer validToken123";
        String jwtToken = "validToken123";
        String userEmail = "test@example.com";

        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.extractToken(token)).thenReturn(jwtToken);

            when(webClientService.fetchData(USER_URL, String.class, jwtToken)).thenReturn(Mono.just(userEmail));

            // Act
            cartService.deleteCart(token);

            // Assert
            verify(cartRepository, times(1)).deleteById(userEmail);
        }
    }
    @Test
    void deleteCart_ShouldThrowException_WhenUserEmailIsEmpty() {
        // Arrange
        String token = "Bearer validToken123";
        String jwtToken = "validToken123";

        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.extractToken(token)).thenReturn(jwtToken);

            when(webClientService.fetchData(USER_URL, String.class, jwtToken)).thenReturn(Mono.just(""));

            // Act & Assert
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                    cartService.deleteCart(token)
            );

            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            assertEquals("Failed to fetch user email or email is empty", exception.getReason());
        }
    }


}