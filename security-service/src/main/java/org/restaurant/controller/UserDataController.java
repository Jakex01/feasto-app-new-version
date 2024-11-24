package org.restaurant.controller;

import lombok.RequiredArgsConstructor;
import org.restaurant.service.UserDataService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UserDataController {

    private final UserDataService userDataService;

    @GetMapping("/{userId}")
    public String getUserEmail(@PathVariable Long userId){
        return userDataService.getUserEmailByUserId(userId);
    }
    @GetMapping String getUserEmailByToken(@RequestParam String token) {
        return userDataService.getUserEmailByToken(token);
    }
}
