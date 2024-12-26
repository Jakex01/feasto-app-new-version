package org.restaurant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.restaurant.exception.InvalidQrDigitsCodeException;
import org.restaurant.model.TokenEntity;
import org.restaurant.model.TokenType;
import org.restaurant.model.UserCredentialEntity;
import org.restaurant.repository.TokenRepository;
import org.restaurant.repository.UserCredentialRepository;
import org.restaurant.request.AuthenticationRequest;
import org.restaurant.request.RegisterRequest;
import org.restaurant.request.VerificationRequest;
import org.restaurant.response.AuthenticationResponse;
import org.restaurant.tfa.TwoFactorAuthenticationService;
import org.restaurant.validator.ObjectsValidator;
import org.springframework.cglib.core.CodeGenerationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserCredentialRepository userCredentialRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TwoFactorAuthenticationService tfaService;
    private final ObjectsValidator<RegisterRequest> registerValidator;
    private final ObjectsValidator<AuthenticationRequest> authenticationValidator;
    private final ObjectsValidator<VerificationRequest> verificationValidator;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getEmail());
        registerValidator.validate(request);

        var user = UserCredentialEntity.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .mfaEnabled(request.isMfaEnabled())
                .build();

        if (request.isMfaEnabled()) {
            user.setSecret(tfaService.generateNewSecret());
            log.info("Generated MFA secret for user: {}", request.getEmail());
        }

        var savedUser = userCredentialRepository.save(user);
        log.info("User successfully saved with ID: {}", savedUser.getId());

        var jwtToken = jwtService.generateToken(user);
        log.debug("Generated JWT token for user: {}", request.getEmail());

        SavedToken(savedUser, jwtToken);

        var refreshToken = jwtService.generateRefreshToken(user);
        log.debug("Generated refresh token for user: {}", request.getEmail());

        log.info("User registration completed for: {}", request.getEmail());
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .mfaEnabled(user.isMfaEnabled())
                .secretImageUri(tfaService.generateQrCodeImageUri(user.getSecret()))
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("Authenticating user: {}", request.getEmail());
        authenticationValidator.validate(request);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userCredentialRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.error("User not found during authentication: {}", request.getEmail());
                    return new UsernameNotFoundException("User not found");
                });

        if (user.isMfaEnabled()) {
            log.info("MFA is enabled for user: {}", request.getEmail());
            return AuthenticationResponse.builder()
                    .accessToken("")
                    .refreshToken("")
                    .mfaEnabled(true)
                    .build();
        }

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        revokeAllUserTokens(user);
        SavedToken(user, jwtToken);

        log.info("User authentication successful: {}", request.getEmail());
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .mfaEnabled(false)
                .build();
    }

    private void revokeAllUserTokens(UserCredentialEntity user) {
        log.debug("Revoking all valid tokens for user ID: {}", user.getId());
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            log.info("No valid tokens found to revoke for user ID: {}", user.getId());
            return;
        }
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
        log.info("All valid tokens revoked for user ID: {}", user.getId());
    }

    private void SavedToken(UserCredentialEntity savedUser, String jwtToken) {
        log.debug("Saving token for user ID: {}", savedUser.getId());
        var token = TokenEntity.builder()
                .userCredentialEntity(savedUser)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();

        tokenRepository.save(token);
        log.info("Token saved successfully for user ID: {}", savedUser.getId());
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("Refreshing token");
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Invalid or missing authorization header");
            return;
        }

        final String refreshToken = authHeader.substring(7);
        final String userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail != null) {
            var userDetails = userCredentialRepository.findByEmail(userEmail)
                    .orElseThrow(() -> {
                        log.error("User not found during token refresh: {}", userEmail);
                        return new UsernameNotFoundException("User not found");
                    });

            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                log.debug("Valid refresh token for user: {}", userEmail);
                var accessToken = jwtService.generateToken(userDetails);

                revokeAllUserTokens(userDetails);
                SavedToken(userDetails, accessToken);

                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .mfaEnabled(false)
                        .build();

                log.info("Token refreshed successfully for user: {}", userEmail);
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            } else {
                log.warn("Invalid refresh token for user: {}", userEmail);
            }
        }
    }

    public AuthenticationResponse verifyCode(VerificationRequest verificationRequest) throws CodeGenerationException, dev.samstevens.totp.exceptions.CodeGenerationException {
        verificationValidator.validate(verificationRequest);
        UserCredentialEntity user = userCredentialRepository.findByEmail(verificationRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException(String.format("No user found with %S", verificationRequest.getEmail()))
                );

        if(tfaService.isOtpNotValid(user.getSecret(), verificationRequest.getCode())) {
            throw new InvalidQrDigitsCodeException("Code is not correct");
        }
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .mfaEnabled(user.isMfaEnabled())
                .build();
    }
    public ResponseEntity<Boolean> validateToken(String token) {
        try {
            String jwt = token.substring(7);
            boolean isValid = jwtService.validateToken(jwt);
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
    public String getCurrentlyLoggedUser(Authentication authentication) {
        UserCredentialEntity principal = (UserCredentialEntity) authentication.getPrincipal();

        UserCredentialEntity userEntity = userCredentialRepository.findById(principal.getId())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        return userEntity.getEmail();
    }
}

