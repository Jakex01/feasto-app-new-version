package org.restaurant.service;

import lombok.RequiredArgsConstructor;
import org.restaurant.mapstruct.LocationMapper;
import org.restaurant.model.LocationEntity;
import org.restaurant.model.UserCredentialEntity;
import org.restaurant.repository.LocationRepository;
import org.restaurant.repository.UserCredentialRepository;
import org.restaurant.request.LocationRequest;
import org.restaurant.response.LocationNamesResponse;
import org.restaurant.response.LocationResponse;
import org.restaurant.validator.ObjectsValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService{

    private final LocationRepository locationRepository;
    private final UserCredentialRepository userCredentialRepository;
    private final ObjectsValidator<LocationRequest> locationRequestObjectsValidator;
    @Override
    public ResponseEntity<LocationResponse> createLocation(LocationRequest request, Authentication authentication) {
        locationRequestObjectsValidator.validate(request);
        UserCredentialEntity principal = (UserCredentialEntity) authentication.getPrincipal();
        UserCredentialEntity userEntity = userCredentialRepository.findById(principal.getId())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        LocationEntity locationEntity = LocationMapper.INSTANCE.locationRequestToLocationEntity(request);
        locationEntity.setUserCredentialEntity(userEntity);
        locationRepository.save(locationEntity);
        LocationResponse locationResponse = LocationMapper.INSTANCE.locationEntityToLocationResponse(locationEntity);
        return ResponseEntity.ok().body(locationResponse);

    }
    @Override
    public ResponseEntity<?> updateLocation(Long id, Authentication authentication) {
        UserCredentialEntity principal = (UserCredentialEntity) authentication.getPrincipal();
        UserCredentialEntity userEntity = userCredentialRepository.findById(principal.getId())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        List<LocationEntity> locationEntities = locationRepository.findAllByUserCredentialEntity(userEntity);
        List<LocationEntity> modifiedLocations = locationEntities.stream()
                .filter(t -> t.isCurrent() != Objects.equals(t.getId(), id))
                .peek(t -> t.setCurrent(Objects.equals(t.getId(), id)))
                .toList();
        locationRepository.saveAll(modifiedLocations);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    @Override
    public ResponseEntity<String> getCurrentLocation(Authentication authentication) {
        UserCredentialEntity principal = (UserCredentialEntity) authentication.getPrincipal();
        UserCredentialEntity userEntity = userCredentialRepository.findById(principal.getId())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
       LocationEntity locationEntity = locationRepository.findByCurrentAndUserCredentialEntity(true, userEntity);
        String location = locationEntity.getStreet() + " " + locationEntity.getStreetNumber() + " " + locationEntity.getCity();
        return ResponseEntity.ok(location);
    }

    @Override
    public ResponseEntity<List<LocationNamesResponse>> getAllUsersLocations(Authentication authentication) {
        UserCredentialEntity principal = (UserCredentialEntity) authentication.getPrincipal();
        UserCredentialEntity userEntity = userCredentialRepository.findById(principal.getId())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        List<LocationNamesResponse> locationEntity = locationRepository.findAllByUserCredentialEntity(userEntity)
                .stream()
                .map(LocationMapper.INSTANCE::locationEntityToLocationNamesResponse)
                .toList();
        return ResponseEntity.ok(locationEntity);
    }
}
