package org.restaurant.controller;

import dev.samstevens.totp.exceptions.CodeGenerationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.restaurant.request.AuthenticationRequest;
import org.restaurant.request.RegisterRequest;
import org.restaurant.request.VerificationRequest;
import org.restaurant.response.AuthenticationResponse;
import org.restaurant.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/security/identity")
@AllArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @GetMapping("/user")
    public String getToken(Authentication principal){
        return authenticationService.getCurrentlyLoggedUser(principal);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request
    ) {
        var response = authenticationService.register(request);
        if(request.isMfaEnabled()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.accepted().build();
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
    @PostMapping("/refresh-token")
    public void refreshToken(
           HttpServletRequest request,
           HttpServletResponse response
    ) throws IOException {
        authenticationService.refreshToken(request,response);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(
            @RequestBody VerificationRequest verificationRequest
    ) throws CodeGenerationException {
        return ResponseEntity.ok(authenticationService.verifyCode(verificationRequest));
    }
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        return authenticationService.validateToken(token);
    }
//    @GetMapping("/roles")
//    public ResponseEntity<List<String>> getUserRoles(@RequestHeader("Authorization") String token) {
//
//        return ResponseEntity.ok(authenticationService.verifyRole(token));
//    }
}
