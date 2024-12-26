package org.restaurant.service.impl;

import io.micrometer.observation.annotation.Observed;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.restaurant.config.UrlConfig;
import org.restaurant.exceptions.AccessDeniedException;
import org.restaurant.exceptions.UserNotFoundException;
import org.restaurant.factory.RestaurantFactory;
import org.restaurant.mapstruct.dto.MapStructMapper;
import org.restaurant.mapstruct.dto.RestaurantMapper;
import org.restaurant.model.RestaurantEntity;
import org.restaurant.repository.RestaurantRepository;
import org.restaurant.repository.UserFavoriteRestaurantRepository;
import org.restaurant.request.CreateRestaurantRequestDuplicate;
import org.restaurant.request.update.UpdateRestaurantRequest;
import org.restaurant.response.ImageResponse;
import org.restaurant.response.RestaurantConversationResponse;
import org.restaurant.response.RestaurantResponse;
import org.restaurant.response.RestaurantsOwnerResponse;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ElasticRestaurantService elasticService;
    private final RestaurantValidator restaurantValidator;
    private final RestaurantFactory restaurantFactory;
    private final UserDetailsClient userDetailsClient;
    private final RestaurantUpdateService updateService;
    private final UserFavoriteRestaurantRepository userFavoriteRestaurantRepository;
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

        Map<String, Long> responseBody = new HashMap<>();
        responseBody.put("restaurantId", savedRestaurant.getRestaurantId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
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
    public ResponseEntity<RestaurantResponse> findRestaurantById(Long restaurantId, String token) {

        String userEmail = userDetailsClient
                .fetchData(urlConfig.getUserService(), String.class, JwtUtil.extractToken(token)).block();

        if (userEmail == null) {
            throw new UserNotFoundException("User not present");
        }

        RestaurantResponse response = restaurantRepository.findById(restaurantId)
                .map(MapStructMapper.INSTANCE::restaurantEntityToRestaurantResponse)
                .map(restaurantResponse -> new RestaurantResponse(
                        restaurantResponse.restaurantId(),
                        restaurantResponse.name(),
                        restaurantResponse.description(),
                        restaurantResponse.phoneNumber(),
                        restaurantResponse.openingHours(),
                        restaurantResponse.rating(),
                        restaurantResponse.foodType(),
                        restaurantResponse.image(),
                        userFavoriteRestaurantRepository.findByRestaurantIdAndUserEmail(restaurantId, userEmail).isPresent(),
                        restaurantResponse.createDate(),
                        restaurantResponse.locations(),
                        restaurantResponse.menuItems(),
                        restaurantResponse.commentsCount()
                ))
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
        restaurantRepository.save(restaurant);
        elasticService.saveRestaurant(restaurant);
        return ResponseEntity.accepted().build();
    }

    @Override
    @SneakyThrows
    public ResponseEntity<Void> updateRestaurantPhoto(MultipartFile file, Long restaurantId) {
        File tempFile = File.createTempFile("temp", null);
        file.transferTo(tempFile);
        ImageResponse imageResponse = restaurantFactory.uploadImageToDrive(tempFile);

        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId).orElseThrow();
        elasticService.updateImageUrl(restaurantId.toString(), imageResponse.getUrl());
        restaurant.setPhotoUrl(imageResponse.getUrl());
        restaurantRepository.save(restaurant);
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<List<RestaurantsOwnerResponse>> getAllRestaurantsOwner(String token) {
        String userEmail = userDetailsClient
                .fetchData(urlConfig.getUserService(), String.class, JwtUtil.extractToken(token)).block();

        if (userEmail == null) {
            throw new UserNotFoundException("User not present");
        }
        List<RestaurantsOwnerResponse> restaurantEntities = restaurantRepository
                .findAll()
                .stream()
                .filter(restaurant -> restaurant.getManagerEmails().contains(userEmail))
                .map(restaurant -> new RestaurantsOwnerResponse(restaurant.getRestaurantId(), restaurant.getName()))
                .toList();

        return ResponseEntity.ok(restaurantEntities);

    }

    @Override
    public ResponseEntity<Map<Long, Boolean>> checkManagers(Set<Long> restaurantIds, String token) {
        String userEmail = userDetailsClient
                .fetchData(urlConfig.getUserService(), String.class, JwtUtil.extractToken(token)).block();
        if (userEmail == null) {
            throw new UserNotFoundException("User not present");
        }

        Map<Long, Boolean> isManagerMap = restaurantRepository.findAllById(restaurantIds).stream()
                .collect(Collectors.toMap(
                        RestaurantEntity::getRestaurantId,
                        restaurant -> restaurant.getManagerEmails().contains(userEmail)
                ));
        return ResponseEntity.ok(isManagerMap);
    }
}