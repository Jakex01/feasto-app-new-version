package org.restaurant.service.impl;

import io.micrometer.observation.annotation.Observed;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.restaurant.config.UrlConfig;
import org.restaurant.exceptions.AccessDeniedException;
import org.restaurant.exceptions.UserNotFoundException;
import org.restaurant.factory.RestaurantFactory;
import org.restaurant.mapstruct.dto.MapStructMapper;
import org.restaurant.mapstruct.dto.RestaurantMapper;
import org.restaurant.model.RestaurantEntity;
import org.restaurant.repository.RestaurantRepository;
import org.restaurant.request.CreateRestaurantRequestDuplicate;
import org.restaurant.request.update.UpdateRestaurantRequest;
import org.restaurant.response.RestaurantConversationResponse;
import org.restaurant.response.RestaurantResponse;
import org.restaurant.service.ElasticRestaurantService;
import org.restaurant.service.RestaurantService;
import org.restaurant.util.JwtUtil;
import org.restaurant.util.UserDetailsClient;
import org.restaurant.validators.RestaurantValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ElasticRestaurantService elasticService;
    private final RestaurantValidator restaurantValidator;
    private final RestaurantFactory restaurantFactory;
    private final UserDetailsClient userDetailsClient;
    private final RestaurantUpdateService updateService;
    private final UrlConfig urlConfig;
    @Observed(name = "create.restaurant")
    @SneakyThrows
    @Transactional
    @Override
    public ResponseEntity<?> createRestaurant(CreateRestaurantRequestDuplicate createRestaurant, String token) {
        restaurantValidator.validateRequest(createRestaurant);

        String userEmail = userDetailsClient
                .fetchData(urlConfig.getUserService(), String.class, JwtUtil.extractToken(token)).block();

        if (userEmail == null) {
            throw new UserNotFoundException("User not present");
        }
        RestaurantEntity restaurantEntity = restaurantFactory.createRestaurant(createRestaurant, userEmail);
        RestaurantEntity savedRestaurant = restaurantRepository.save(restaurantEntity);
        elasticService.saveRestaurant(savedRestaurant);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @Observed(name = "get.restaurants")
    @Override
    public ResponseEntity<Page<RestaurantEntity>> getAllRestaurants(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RestaurantEntity> restaurantPage = restaurantRepository.findAll(pageable);
        return ResponseEntity.ok(restaurantPage);
    }
    @Transactional
    @Override
    public void deleteRestaurantById(Long restaurantId) {
        elasticService.deleteById(String.valueOf(restaurantId));
        restaurantRepository.deleteById(restaurantId);
    }
    @Observed(name = "getById.restaurant")
    @Override
    public ResponseEntity<RestaurantResponse> findRestaurantById(Long restaurantId) {

        RestaurantResponse response = restaurantRepository.findById(restaurantId)
                .map(MapStructMapper.INSTANCE::restaurantEntityToRestaurantResponse)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with id: " + restaurantId));

        return ResponseEntity.ok(response);
    }
    @Transactional
    public void updateAverageRatingsAndPricing() {
        restaurantRepository.findAll().forEach(restaurant -> {
            updateService.updateRatingsAndPrices(restaurant);
            restaurantRepository.save(restaurant);
            elasticService.saveRestaurant(restaurant);
        });
    }
    @Override
    public Set<RestaurantConversationResponse> findRestaurantsByIds(Set<Long> ids) {
        return restaurantRepository.findAllById(ids).stream()
                .map(restaurant -> new RestaurantConversationResponse(restaurant.getRestaurantId(), restaurant.getName()))
                .collect(Collectors.toSet());
    }
    @Override
    public ResponseEntity<Void> updateRestaurantById(Long restaurantId, UpdateRestaurantRequest updateRestaurantRequest, String token) {
        restaurantValidator.validateRequest(updateRestaurantRequest);
        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with id: " + restaurantId));
        String userEmail = userDetailsClient
                .fetchData(urlConfig.getUserService(), String.class, JwtUtil.extractToken(token)).block();
        if (userEmail == null) {
            throw new UserNotFoundException("User not present");
        }
        if (!restaurant.getManagerEmails().contains(userEmail)) {
            throw new AccessDeniedException("User is not authorized to update this restaurant");
        }

        RestaurantMapper.INSTANCE.updateRestaurantFromRequest(updateRestaurantRequest, restaurant);
        return ResponseEntity.accepted().build();
    }
}