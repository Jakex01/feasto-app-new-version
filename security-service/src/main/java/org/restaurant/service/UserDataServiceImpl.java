package org.restaurant.service;

import lombok.AllArgsConstructor;
import org.restaurant.exception.UserNotFoundException;
import org.restaurant.repository.UserCredentialRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDataServiceImpl implements UserDataService{
    private final UserCredentialRepository userCredentialRepository;
    @Override
    public String getUserEmailByUserId(Long userId) {
        return userCredentialRepository
                .findEmailByUserId(userId)
                .orElseThrow(()-> new UserNotFoundException("user not found: " + userId));
    }
}
