package org.places.controller;

import lombok.RequiredArgsConstructor;
import org.places.service.GooglePlacesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PlacesController {

    private final GooglePlacesService googlePlacesService;


    @GetMapping("/places/search")
    public String searchPlaces(@RequestParam String query) {
        return googlePlacesService.searchPlaces(query);
    }

}
