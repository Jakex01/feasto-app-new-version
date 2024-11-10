package org.restaurant.service;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class GoogleMapsService {

    private final GeoApiContext geoApiContext;


    public String calculateTravelTime(String origin, String destination, TravelMode mode) throws InterruptedException, ApiException, IOException {
        DirectionsResult result = DirectionsApi.newRequest(geoApiContext)
                .mode(mode)
                .origin(origin)
                .destination(destination)
                .await();

        if (result.routes != null && result.routes.length > 0) {
            DirectionsRoute route = result.routes[0];
            return "Estimated travel time: " + route.legs[0].duration.humanReadable;
        }

        return "No routes found";
    }

}
