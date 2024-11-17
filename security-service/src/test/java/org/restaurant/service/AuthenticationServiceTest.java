package org.restaurant.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.restaurant.exception.ObjectNotValidException;
import org.restaurant.model.Role;
import org.restaurant.model.UserCredentialEntity;
import org.restaurant.repository.TokenRepository;
import org.restaurant.repository.UserCredentialRepository;
import org.restaurant.request.RegisterRequest;
import org.restaurant.response.AuthenticationResponse;
import org.restaurant.tfa.TwoFactorAuthenticationService;
import org.restaurant.validator.ObjectsValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.mockito.Mockito.when;
import static org.restaurant.model.Role.USER;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private UserCredentialRepository userCredentialRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private TwoFactorAuthenticationService tfaService;

    @Mock
    private ObjectsValidator<RegisterRequest> registerValidator;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .role(USER)
                .mfaEnabled(true)
                .build();
    }

    @Test
    void testRegisterSuccess() {
        // Mock dependencies
        String encodedPassword = "encodedPassword123";
        String jwtToken = "mockedJwtToken";
        String refreshToken = "mockedRefreshToken";
        String secret = "mockedSecret";
        String qrCodeImageUri = "mockedQrCodeUri";

        UserCredentialEntity savedUser = UserCredentialEntity.builder()
                .firstname(registerRequest.getFirstname())
                .lastname(registerRequest.getLastname())
                .email(registerRequest.getEmail())
                .password(encodedPassword)
                .role(registerRequest.getRole())
                .mfaEnabled(registerRequest.isMfaEnabled())
                .secret(secret)
                .build();

        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn(encodedPassword);
        when(userCredentialRepository.save(any(UserCredentialEntity.class))).thenReturn(savedUser);
        when(jwtService.generateToken(savedUser)).thenReturn(jwtToken);
        when(jwtService.generateRefreshToken(savedUser)).thenReturn(refreshToken);
        when(tfaService.generateNewSecret()).thenReturn(secret);
        when(tfaService.generateQrCodeImageUri(secret)).thenReturn(qrCodeImageUri);

        // Execute the method
        AuthenticationResponse response = authenticationService.register(registerRequest);

        // Verify interactions
        verify(registerValidator).validate(registerRequest);
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userCredentialRepository).save(any(UserCredentialEntity.class));
        verify(jwtService).generateToken(savedUser);
        verify(jwtService).generateRefreshToken(savedUser);
        verify(tfaService).generateNewSecret();
        verify(tfaService).generateQrCodeImageUri(secret);

        // Assertions
        assertNotNull(response);
        assertEquals(jwtToken, response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());
        assertTrue(response.isMfaEnabled());
        assertEquals(qrCodeImageUri, response.getSecretImageUri());
    }
    @Test
    void validate_WhenAllFieldsAreValid_ShouldPass() {
        RegisterRequest request = RegisterRequest.builder()
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .password("securePassword123")
                .role(Role.USER)
                .mfaEnabled(false)
                .build();

        assertDoesNotThrow(() -> registerValidator.validate(request));
    }



}