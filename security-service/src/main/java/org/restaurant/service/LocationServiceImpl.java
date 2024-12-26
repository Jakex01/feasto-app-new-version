package org.restaurant.service;

import lombok.RequiredArgsConstructor;
import org.restaurant.exception.MissingTokenException;
import org.restaurant.exception.UserNotFoundException;
import org.restaurant.mapstruct.RestaurantLocationMapper;
import org.restaurant.model.LocationEntity;
import org.restaurant.model.UserCredentialEntity;
import org.restaurant.repository.LocationRepository;
import org.restaurant.repository.UserCredentialRepository;
import org.restaurant.request.LocationRequest;
import org.restaurant.response.LocationNamesResponse;
import org.restaurant.response.LocationResponse;
import org.restaurant.util.ErrorMessages;
import org.restaurant.validator.ObjectsValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService{

    private final LocationRepository locationRepository;
    private final UserDataServiceImpl userDataService;
    private final UserCredentialRepository userCredentialRepository;
    private final ObjectsValidator<LocationRequest> locationRequestObjectsValidator;
    @Override
    public ResponseEntity<LocationResponse> createLocation(LocationRequest request, String token) {
        locationRequestObjectsValidator.validate(request);
        if (token == null || token.isEmpty()) {
            throw new MissingTokenException(ErrorMessages.MISSING_TOKEN);
        }
        String userEmail = userDataService.getUserEmailByToken(token);
        UserCredentialEntity userEntity = userCredentialRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessages.USER_NOT_FOUND));

        LocationEntity locationEntity = RestaurantLocationMapper.INSTANCE.locationRequestToLocationEntity(request);
        locationEntity.setUserCredentialEntity(userEntity);
        locationRepository.save(locationEntity);
        LocationResponse locationResponse = RestaurantLocationMapper.INSTANCE.locationEntityToLocationResponse(locationEntity);
        return ResponseEntity.ok().body(locationResponse);
    }
    @Override
    public ResponseEntity<?> updateLocation(Long id, String token) {
        String userEmail = userDataService.getUserEmailByToken(token);
        UserCredentialEntity userEntity = userCredentialRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessages.USER_NOT_FOUND));

        List<LocationEntity> locationEntities = locationRepository.findAllByUserCredentialEntity(userEntity);
        List<LocationEntity> modifiedLocations = locationEntities.stream()
                .filter(t -> t.isCurrent() != Objects.equals(t.getId(), id))
                .peek(t -> t.setCurrent(Objects.equals(t.getId(), id)))
                .toList();
        locationRepository.saveAll(modifiedLocations);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    @Override
    public ResponseEntity<String> getCurrentLocation(String token) {
        String userEmail = userDataService.getUserEmailByToken(token);
        UserCredentialEntity userEntity = userCredentialRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessages.USER_NOT_FOUND));
       Optional<LocationEntity> locationEntityOptional = locationRepository.findByCurrentAndUserCredentialEntity(true, userEntity);
        if (locationEntityOptional.isPresent()) {
            LocationEntity locationEntity = locationEntityOptional.get();
            String location = locationEntity.getStreet() + " " + locationEntity.getStreetNumber() + " " + locationEntity.getCity();
            return ResponseEntity.ok(location);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Current location not found");
        }
    }

    @Override
    public ResponseEntity<List<LocationResponse>> getAllUsersLocations(String token) {
        String userEmail = userDataService.getUserEmailByToken(token);
        UserCredentialEntity userEntity = userCredentialRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessages.USER_NOT_FOUND));

        List<LocationResponse> locationEntity = locationRepository.findAllByUserCredentialEntity(userEntity)
                .stream()
                .map(RestaurantLocationMapper.INSTANCE::locationEntityToLocationResponse)
                .toList();
        return ResponseEntity.ok(locationEntity);
    }

    @Override
    public ResponseEntity<List<LocationNamesResponse>> getAllUsersShortenLocationsList(String token) {
        String userEmail = userDataService.getUserEmailByToken(token);
        UserCredentialEntity userEntity = userCredentialRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessages.USER_NOT_FOUND));

        List<LocationNamesResponse> locationEntity = locationRepository.findAllByUserCredentialEntity(userEntity)
                .stream()
                .map(RestaurantLocationMapper.INSTANCE::locationEntityToLocationNamesResponse)
                .toList();
        return ResponseEntity.ok(locationEntity);
    }
}
