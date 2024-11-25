package org.restaurant.service;

import lombok.AllArgsConstructor;
import org.restaurant.exception.UserNotFoundException;
import org.restaurant.repository.UserCredentialRepository;
import org.restaurant.util.JwtUtil;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDataServiceImpl implements UserDataService{
    private final UserCredentialRepository userCredentialRepository;
    private final JwtService jwtService;
    @Override
    public String getUserEmailByUserId(Long userId) {
        return userCredentialRepository
                .findEmailByUserId(userId)
                .orElseThrow(()-> new UserNotFoundException("user not found: " + userId));
    }

    @Override
    public String getUserEmailByToken(String token) {
      String jwtToken = JwtUtil.extractToken(token);
        return jwtService.extractUsername(jwtToken);
    }
}
