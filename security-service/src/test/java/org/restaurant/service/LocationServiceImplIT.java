package org.restaurant.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.restaurant.model.LocationEntity;
import org.restaurant.model.UserCredentialEntity;
import org.restaurant.repository.LocationRepository;
import org.restaurant.repository.UserCredentialRepository;
import org.restaurant.request.LocationRequest;
import org.restaurant.response.LocationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class LocationServiceImplIT {

    @Autowired
    private LocationServiceImpl locationService;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private UserCredentialRepository userCredentialRepository;
    @MockBean
    private UserDataServiceImpl userDataService;
    @LocalServerPort
    private Integer port;

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        System.out.println("hello");
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }


    @AfterEach
    void cleanUp() {
        locationRepository.deleteAll();
        userCredentialRepository.deleteAll();
    }

    @Test
    void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void shouldCreateLocationSuccessfully() {
        // Given
        UserCredentialEntity user = UserCredentialEntity.builder()
                .email("user@example.com")
                .password("defaultPassword")
                .build();

        userCredentialRepository.save(user);

        LocationRequest locationRequest = new LocationRequest(
                "Springfield",
                "Main Street",
                "123A",
                "Home",
                "USA",
                "12-345",
                true
        );

        String token = "valid-token";
        when(userDataService.getUserEmailByToken(anyString())).thenReturn("user@example.com");
        // When
        ResponseEntity<LocationResponse> response = locationService.createLocation(locationRequest, token);

        // Then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().street()).isEqualTo("Main Street");
        assertThat(response.getBody().city()).isEqualTo("Springfield");
        assertThat(response.getBody().country()).isEqualTo("USA");

        LocationEntity savedLocation = locationRepository.findAll().get(0);
        assertThat(savedLocation).isNotNull();
        assertThat(savedLocation.getStreet()).isEqualTo("Main Street");
        assertThat(savedLocation.getUserCredentialEntity().getEmail()).isEqualTo("user@example.com");
    }
}
