package org.restaurant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.restaurant.model.Role;
import org.restaurant.model.TokenEntity;
import org.restaurant.model.TokenType;
import org.restaurant.model.UserCredentialEntity;
import org.restaurant.repository.TokenRepository;
import org.restaurant.repository.UserCredentialRepository;
import org.restaurant.request.AuthenticationRequest;
import org.restaurant.request.RegisterRequest;
import org.restaurant.response.AuthenticationResponse;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserCredentialRepository userCredentialRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private AmqpTemplate rabbitTemplate;
    private TopicExchange userExchange;
    public AuthenticationResponse register(RegisterRequest request) {
    var user = UserCredentialEntity.builder()
            .firstname(request.getFirstname())
            .lastname(request.getLastname())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole())
            .build();

       var savedUser =  userCredentialRepository.save(user);

    var jwtToken =  jwtService.generateToken(user);
        SavedToken(savedUser, jwtToken);

    var refreshToken = jwtService.generateRefreshToken(user);

        return AuthenticationResponse.builder()
            .accessToken(jwtToken)
                .refreshToken(refreshToken)
            .build();
    }
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user  = userCredentialRepository.findByEmail(request.getEmail())
                .orElseThrow();

        var jwtToken =  jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        revokeAllUserTokens(user);
        SavedToken(user, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
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
}
