package org.restaurant.service

import org.restaurant.exception.MissingTokenException
import org.restaurant.exception.UserNotFoundException
import org.restaurant.model.LocationEntity
import org.restaurant.model.UserCredentialEntity
import org.restaurant.repository.LocationRepository
import org.restaurant.repository.UserCredentialRepository
import org.restaurant.request.LocationRequest
import org.restaurant.response.LocationNamesResponse
import org.restaurant.response.LocationResponse
import org.restaurant.util.ErrorMessages
import org.restaurant.validator.ObjectsValidator
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification

class LocationServiceImplTest extends Specification {
    def locationRepository = Mock(LocationRepository)
    def userDataService = Mock(UserDataServiceImpl)
    def userCredentialRepository = Mock(UserCredentialRepository)
    def locationRequestValidator = Mock(ObjectsValidator)
    def locationService = new LocationServiceImpl(locationRepository, userDataService, userCredentialRepository, locationRequestValidator)

    def "Successfully created location"() {
        given:
        def request = new LocationRequest(
                "Warsaw",
                "Nowy Swiat",
                "10A",
                "Main Office",
                "Poland",
                "00-001",
                true
        )
        def token = "valid-token"

        def userEntity = new UserCredentialEntity(email: "test@example.com")

        def locationEntity = new LocationEntity()
        locationEntity.setCity(request.city())
        locationEntity.setStreet(request.street())
        locationEntity.setStreetNumber(request.streetNumber())
        locationEntity.setLocationName(request.locationName())
        locationEntity.setCountry(request.country())
        locationEntity.setPostalCode(request.postalCode())
        locationEntity.setCurrent(request.current())
        locationEntity.setUserCredentialEntity(userEntity)

        def savedLocationEntity = locationEntity.toBuilder().id(1L).build()

        def locationResponse = new LocationResponse(
                null,
                savedLocationEntity.getCity(),
                savedLocationEntity.getStreet(),
                savedLocationEntity.getStreetNumber(),
                savedLocationEntity.getCountry(),
                savedLocationEntity.getLocationName(),
                savedLocationEntity.getPostalCode(),
                savedLocationEntity.isCurrent()
        )

        userDataService.getUserEmailByToken(token) >> "test@example.com"
        userCredentialRepository.findByEmail("test@example.com") >> Optional.of(userEntity)
        locationRepository.save(_ as LocationEntity) >> savedLocationEntity

        when:
        def response = locationService.createLocation(request, token)

        then:
        response.getBody() == locationResponse
        1 * locationRequestValidator.validate(request)
        1 * locationRepository.save(_ as LocationEntity)
    }
    def "Should throw UserNotFoundException when user not found"() {
        given:
        def request = new LocationRequest(
                "Warsaw",
                "Nowy Swiat",
                "10A",
                "Main Office",
                "Poland",
                "00-001",
                true
        )
        def token = "invalid-token"

        userDataService.getUserEmailByToken(token) >> "nonexistent@example.com"
        userCredentialRepository.findByEmail("nonexistent@example.com") >> Optional.empty()

        when:
        locationService.createLocation(request, token)

        then:
        def exception = thrown(UserNotFoundException)
        exception.message == "User not found"
    }

    def "Should handle exception thrown by userDataService when token is invalid"() {
        given:
        def request = new LocationRequest(
                "Warsaw",
                "Nowy Swiat",
                "10A",
                "Main Office",
                "Poland",
                "00-001",
                true
        )
        def token = "invalid-token"

        userDataService.getUserEmailByToken(token) >> { throw new MissingTokenException(ErrorMessages.MISSING_TOKEN) }

        when:
        locationService.createLocation(request, token)

        then:
        def exception = thrown(MissingTokenException)
        exception.message == "Authorization token is missing or empty"
    }



    def "should update location and set the correct location as current"() {
        given: "a user email from token and their locations"
        def token = "valid-token"
        def userEmail = "user@example.com"
        def userEntity = new UserCredentialEntity(email: userEmail)
        def location1 = new LocationEntity(id: 1L, current: true)
        def location2 = new LocationEntity(id: 2L, current: false)
        def locationEntities = [location1, location2]

        and: "the target location id to update"
        def targetLocationId = 2L

        userDataService.getUserEmailByToken(token) >> userEmail
        userCredentialRepository.findByEmail(userEmail) >> Optional.of(userEntity)
        locationRepository.findAllByUserCredentialEntity(userEntity) >> locationEntities

        when: "the location is updated"
        ResponseEntity<?> response = locationService.updateLocation(targetLocationId, token)

        then: "the correct location is set to current"
        1 * locationRepository.saveAll({ List<LocationEntity> locations ->
            locations.size() == 2 &&
                    locations.any { it.id == 1L && !it.current } &&
                    locations.any { it.id == 2L && it.current }
        })

        and: "the response is HTTP ACCEPTED"
        response.statusCode == HttpStatus.ACCEPTED
    }


    def "Should throw UserNotFoundException if user is not found"() {
        given: "An invalid token"
        def token = "invalid-token"

        and: "User not found for the token"
        userDataService.getUserEmailByToken(token) >> "unknown@example.com"
        userCredentialRepository.findByEmail("unknown@example.com") >> Optional.empty()

        when: "updateLocation is called"
        locationService.updateLocation(1L, token)

        then: "UserNotFoundException is thrown"
        def exception = thrown(UserNotFoundException)
        exception.message == ErrorMessages.USER_NOT_FOUND
    }

    def "should return the current location for the user"() {
        given: "a valid token and user email"
        def token = "valid-token"
        def userEmail = "user@example.com"
        def userEntity = new UserCredentialEntity(email: userEmail)

        and: "a current location entity for the user"
        def currentLocation = Optional.of(new LocationEntity(
                street: "Main St",
                streetNumber: "123",
                city: "Springfield",
                current: true
        ))
        locationRepository.findByCurrentAndUserCredentialEntity(true, userEntity) >> currentLocation


        userDataService.getUserEmailByToken(token) >> userEmail
        userCredentialRepository.findByEmail(userEmail) >> Optional.of(userEntity)
        locationRepository.findByCurrentAndUserCredentialEntity(true, userEntity) >> currentLocation

        when: "the current location is retrieved"
        ResponseEntity<String> response = locationService.getCurrentLocation(token)

        then: "the response contains the correct location"
        response.getBody() == "Main St 123 Springfield"
        response.getStatusCodeValue() == 200
    }

    def "should throw UserNotFoundException if user is not found"() {
        given: "a token for a non-existent user"
        def token = "invalid-token"

        userDataService.getUserEmailByToken(token) >> "nonexistent@example.com"
        userCredentialRepository.findByEmail("nonexistent@example.com") >> Optional.empty()

        when: "attempting to retrieve the current location"
        locationService.getCurrentLocation(token)

        then: "a UserNotFoundException is thrown"
        thrown(UserNotFoundException)
    }

    def "should return 404 if no current location exists"() {
        given: "a valid token and user email"
        def token = "valid-token"
        def userEmail = "user@example.com"
        def userEntity = new UserCredentialEntity(email: userEmail)

        userDataService.getUserEmailByToken(token) >> userEmail
        userCredentialRepository.findByEmail(userEmail) >> Optional.of(userEntity)
        locationRepository.findByCurrentAndUserCredentialEntity(true, userEntity) >> Optional.empty()

        when: "attempting to retrieve the current location"
        ResponseEntity<String> response = locationService.getCurrentLocation(token)

        then: "the response contains a 404 status and appropriate message"
        response.getStatusCode() == HttpStatus.NOT_FOUND
        response.getBody() == "Current location not found"
    }

    def "should return all user locations"() {
        given: "a valid token and user email"
        def token = "valid-token"
        def userEmail = "user@example.com"
        def userEntity = new UserCredentialEntity(email: userEmail)

        and: "a list of location entities for the user"
        def locationEntities = [
                new LocationEntity(
                        id: 1L,
                        city: "Springfield",
                        street: "Main St",
                        streetNumber: "123",
                        country: "USA",
                        locationName: "Home",
                        postalCode: "12345",
                        current: true,
                        userCredentialEntity: userEntity
                ),
                // Add more LocationEntity instances as needed
        ]

        and: "expected location responses"
        def expectedResponses = locationEntities.collect {
            new LocationResponse(
                    it.id,
                    it.city,
                    it.street,
                    it.streetNumber,
                    it.country,
                    it.locationName,
                    it.postalCode,
                    it.current
            )
        }

        userDataService.getUserEmailByToken(token) >> userEmail
        userCredentialRepository.findByEmail(userEmail) >> Optional.of(userEntity)
        locationRepository.findAllByUserCredentialEntity(userEntity) >> locationEntities

        when: "retrieving all user locations"
        ResponseEntity<List<LocationResponse>> response = locationService.getAllUsersLocations(token)

        then: "the response contains the correct location data"
        response.getBody() == expectedResponses
        response.getStatusCodeValue() == 200
    }


    def "should return empty list when user has no locations"() {
        given: "a valid token and user email"
        def token = "valid-token"
        def userEmail = "user@example.com"
        def userEntity = new UserCredentialEntity(email: userEmail)

        when: "the user email is retrieved by token"
        userDataService.getUserEmailByToken(token) >> userEmail

        and: "the user entity is found by email"
        userCredentialRepository.findByEmail(userEmail) >> Optional.of(userEntity)

        and: "no location entities are found for the user"
        locationRepository.findAllByUserCredentialEntity(userEntity) >> []

        and: "the service retrieves all user locations"
        ResponseEntity<List<LocationResponse>> response = locationService.getAllUsersLocations(token)

        then: "the response contains an empty list"
        response.getBody().isEmpty()
        response.getStatusCodeValue() == 200
    }

    def "should throw UserNotFoundException when user does not exist"() {
        given: "a valid token and user email"
        def token = "valid-token"
        def userEmail = "user@example.com"

        when: "the user email is retrieved by token"
        userDataService.getUserEmailByToken(token) >> userEmail

        and: "no user entity is found by email"
        userCredentialRepository.findByEmail(userEmail) >> Optional.empty()

        and: "the service attempts to retrieve all user locations"
        locationService.getAllUsersLocations(token)

        then: "a UserNotFoundException is thrown"
        def e = thrown(UserNotFoundException)
        e.message == ErrorMessages.USER_NOT_FOUND
    }
    def "should return all user's shortened locations list"() {
        given: "a valid token and user email"
        def token = "valid-token"
        def userEmail = "user@example.com"
        def userEntity = new UserCredentialEntity(email: userEmail)

        and: "a list of location entities associated with the user"
        def locationEntities = [
                new LocationEntity(id: 1L, locationName: "Home"),
                new LocationEntity(id: 2L, locationName: "Office")
        ]

        and: "the expected list of LocationNamesResponse"
        def expectedResponses = locationEntities.collect {
            new LocationNamesResponse(it.id, it.locationName)
        }

        userDataService.getUserEmailByToken(token) >> userEmail
        userCredentialRepository.findByEmail(userEmail) >> Optional.of(userEntity)
        locationRepository.findAllByUserCredentialEntity(userEntity) >> locationEntities

        when: "retrieving all user's shortened locations list"
        ResponseEntity<List<LocationNamesResponse>> response = locationService.getAllUsersShortenLocationsList(token)

        then: "the response contains the expected list of LocationNamesResponse"
        response.getBody() == expectedResponses
        response.getStatusCodeValue() == 200
    }


}
