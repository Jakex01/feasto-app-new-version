package org.restaurant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
@RequiredArgsConstructor
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
        registerValidator.validate(request);
    var user = UserCredentialEntity.builder()
            .firstname(request.getFirstname())
            .lastname(request.getLastname())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole())
            .mfaEnabled(request.isMfaEnabled())
            .build();

    if(request.isMfaEnabled()) {
        user.setSecret(tfaService.generateNewSecret());
    }

    var savedUser =  userCredentialRepository.save(user);
    var jwtToken =  jwtService.generateToken(user);
        SavedToken(savedUser, jwtToken);
    var refreshToken = jwtService.generateRefreshToken(user);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .mfaEnabled(user.isMfaEnabled())
                .secretImageUri(tfaService.generateQrCodeImageUri(user.getSecret()))
            .build();
    }
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationValidator.validate(request);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user  = userCredentialRepository.findByEmail(request.getEmail())
                .orElseThrow();

        if(user.isMfaEnabled()) {
            return AuthenticationResponse.builder()
                    .accessToken("")
                    .refreshToken("")
                    .mfaEnabled(true)
                    .build();
        }

        var jwtToken =  jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        revokeAllUserTokens(user);
        SavedToken(user, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .mfaEnabled(false)
                .build();

    }
    private void revokeAllUserTokens(UserCredentialEntity user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
    private void SavedToken(UserCredentialEntity savedUser, String jwtToken) {
        var token = TokenEntity
                .builder()
                .userCredentialEntity(savedUser)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();

        tokenRepository.save(token);
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if(authHeader==null || authHeader.startsWith("Bearer ")){
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);

        if(userEmail!=null ){
            var userDetails = this.userCredentialRepository.findByEmail(userEmail).orElseThrow();

            if(jwtService.isTokenValid(refreshToken, userDetails) ){
            var accessToken = jwtService.generateToken(userDetails);

            revokeAllUserTokens(userDetails);
            SavedToken(userDetails, accessToken);

            var authResponse = AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .mfaEnabled(false)
                    .build();
            new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public String getCurrentlyLoggedUser(Authentication authentication) {
        UserCredentialEntity principal = (UserCredentialEntity) authentication.getPrincipal();

        UserCredentialEntity userEntity = userCredentialRepository.findById(principal.getId())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        return userEntity.getEmail();
    }

    public AuthenticationResponse verifyCode(VerificationRequest verificationRequest) throws CodeGenerationException {
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
}
