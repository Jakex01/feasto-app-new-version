package org.places.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GooglePlacesService {

    private final RestTemplate restTemplate;
    @Value("${google.places.api-key}")
    private String apiKey;

    public String searchPlaces(String query) {
        String url = String.format("https://maps.googleapis.com/maps/api/place/textsearch/json?query=%s&key=%s", query, apiKey);
        return restTemplate.getForObject(url, String.class);
    }

}
