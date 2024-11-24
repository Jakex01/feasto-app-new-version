package org.restaurant.service;

public interface UserDataService {
    String getUserEmailByUserId(Long userId);

    String getUserEmailByToken(String token);

}
