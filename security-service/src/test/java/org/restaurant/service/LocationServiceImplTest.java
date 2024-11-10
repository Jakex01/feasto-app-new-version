package org.restaurant.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.restaurant.mapstruct.LocationMapper;
import org.restaurant.model.LocationEntity;
import org.restaurant.model.UserCredentialEntity;
import org.restaurant.repository.LocationRepository;
import org.restaurant.repository.UserCredentialRepository;
import org.restaurant.request.LocationRequest;
import org.restaurant.response.LocationResponse;
import org.restaurant.validator.ObjectsValidator;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class LocationServiceImplTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private UserCredentialRepository userCredentialRepository;

    @Mock
    private ObjectsValidator<LocationRequest> locationRequestObjectsValidator;

    @InjectMocks
    private LocationServiceImpl locationService;

//    @Test
//    public void testCreateLocation() {
//        // Arrange
//        Long userId = 1L;  // Static user ID
//        UserCredentialEntity user = new UserCredentialEntity();
//        user.setId(userId);  // Setting up the user with the static ID
//        LocationRequest request = new LocationRequest("City", "Street", "123", "Country", "12345", false);
//
//        LocationEntity savedEntity = new LocationEntity();
//        savedEntity.setUserCredentialEntity(user);  // Linking user to the location
//
//        LocationResponse expectedResponse = new LocationResponse(1L,"City", "Street", "123", "Country", "12345", false);
//
//        when(userCredentialRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(LocationMapper.INSTANCE.locationRequestToLocationEntity(request)).thenReturn(savedEntity);
//        when(locationRepository.save(savedEntity)).thenReturn(savedEntity);
//        when(LocationMapper.INSTANCE.locationEntityToLocationResponse(savedEntity)).thenReturn(expectedResponse);
//
//        // Act
//        ResponseEntity<LocationResponse> response = locationService.createLocation(request, user);
//
//        // Assert
//        Assertions.assertNotNull(response);
//        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
//        Assertions.assertEquals(expectedResponse, response.getBody());
//
//        verify(locationRequestObjectsValidator).validate(request);
//        verify(userCredentialRepository).findById(userId);
//        verify(locationRepository).save(savedEntity);
//    }

//    @Test
//    public void testUpdateLocation() {
//        // Given
//        Long id = 1L;
//        Authentication auth = mock(Authentication.class);
//        UserCredentialEntity user = new UserCredentialEntity();
//        user.setId(id);
//
//        when(auth.getPrincipal()).thenReturn(user);
//        when(userCredentialRepository.findById(id)).thenReturn(Optional.of(user));
//        when(locationRepository.findAllByUserCredentialEntity(user)).thenReturn(Arrays.asList(
//                new LocationEntity(1L, "City", "Street", "123", "Country", "12345", true, user),
//                new LocationEntity(2L, "City2", "Street2", "124", "Country2", "12346", false, user)
//        ));
//
//        // When
//        ResponseEntity<?> response = locationService.updateLocation(id, auth);
//
//        // Then
//        Assertions.assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
//        verify(locationRepository).saveAll(anyList()); // Check if it saves the updated list
//    }
}