package org.restaurant.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import io.micrometer.observation.annotation.Observed;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.restaurant.exceptions.RestaurantSearchException;
import org.restaurant.mapstruct.dto.LocationMapper;
import org.restaurant.mapstruct.dto.MapStructMapper;
import org.restaurant.mapstruct.dto.MenuItemMapper;
import org.restaurant.mapstruct.dto.RestaurantMapper;
import org.restaurant.model.LocationEntity;
import org.restaurant.model.MenuItemEntity;
import org.restaurant.model.RestaurantEntity;
import org.restaurant.model.elasticsearch.ElasticRestaurant;
import org.restaurant.repository.RestaurantRepository;
import org.restaurant.repository.elasticsearch.ElasticRestaurantRepository;
import org.restaurant.request.CreateRestaurantRequest;
import org.restaurant.request.CreateRestaurantRequestDuplicate;
import org.restaurant.request.FilterRestaurant;
import org.restaurant.response.RestaurantConversationResponse;
import org.restaurant.response.RestaurantResponse;
import org.restaurant.util.ElasticSearchQueryHelper;
import org.restaurant.validators.ObjectsValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.restaurant.util.RestaurantUtils.*;


@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ElasticRestaurantRepository elasticRepository;
    private final ObjectsValidator<CreateRestaurantRequest> objectsValidator;
    private final ElasticsearchClient elasticsearchClient;
    private static final String RESTAURANT_INDEX = "restaurants";
    @Observed(name = "create.restaurant")
    @Transactional
    public ResponseEntity<?> createRestaurant(CreateRestaurantRequestDuplicate createRestaurant) {

        RestaurantEntity restaurantEntity = RestaurantMapper.INSTANCE
                .restaurantRequestToRestaurantEntity(createRestaurant.restaurantInfo());

        LocationEntity locationEntity = LocationMapper.INSTANCE.
                locationRequestToLocationEntity(createRestaurant.restaurantLocation());
        locationEntity.setRestaurant(restaurantEntity);
        List<MenuItemEntity> menuItemEntities = createRestaurant
                .restaurantMenuItems()
                .stream()
                .map(MenuItemMapper.INSTANCE::menuItemRequestToMenuItemEntity)
                .toList();

        restaurantEntity.setLocations(List.of(locationEntity));
        restaurantEntity.setMenuItems(menuItemEntities);

        RestaurantEntity savedRestaurant = restaurantRepository.save(restaurantEntity);

        ElasticRestaurant elasticRestaurant = ElasticRestaurant.builder()
                .id(String.valueOf(savedRestaurant.getRestaurantId()))
                .name(savedRestaurant.getName())
                .description(savedRestaurant.getDescription())
                .foodType(savedRestaurant.getFoodType())
                .image(savedRestaurant.getImage())
                .openingHours(savedRestaurant.getOpeningHours())
                .prices(savedRestaurant.getPrices())
                .build();

        elasticRepository.save(elasticRestaurant);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @Observed(name = "get.restaurants")
    public ResponseEntity<Page<ElasticRestaurant>> getAllRestaurants(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<ElasticRestaurant> restaurantPage = elasticRepository.findAll(pageable);
        return ResponseEntity.ok(restaurantPage);
    }
    public Page<ElasticRestaurant> filterRestaurants(FilterRestaurant filterRestaurant, int page, int size) throws RestaurantSearchException {
        try {
            Pageable pageable = PageRequest.of(page, size);
            int from = (int) pageable.getOffset();
            SearchResponse<ElasticRestaurant> response = elasticsearchClient.search(s -> s
                            .index(RESTAURANT_INDEX)
                            .from(from)
                            .size(size)
                            .query(q -> q
                                    .bool(b -> ElasticSearchQueryHelper.buildQuery(b, filterRestaurant)))
                            .sort(so -> so.field(f -> f.field(filterRestaurant.sort()).order(SortOrder.Desc))),
                    ElasticRestaurant.class
            );
            List<ElasticRestaurant> results = response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();
            assert response.hits().total() != null;
            long totalHits = response.hits().total().value();
            return new PageImpl<>(results, pageable, totalHits);
        } catch (Exception e) {
            throw new RestaurantSearchException("Failed to search for restaurants", e);
        }
    }
    public ElasticRestaurant saveRestaurant(ElasticRestaurant restaurant) {
        return elasticRepository.save(restaurant);
    }
    public void deleteAllRestaurants() {
        elasticRepository.deleteAll();
    }

    @Observed(name = "getById.restaurant")
    public ResponseEntity<RestaurantResponse> findRestaurantById(Long restaurantId) {

        RestaurantResponse response = restaurantRepository.findById(restaurantId)
                .map(MapStructMapper.INSTANCE::restaurantEntityToRestaurantResponse)
//                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with id: " + restaurantId));

        return ResponseEntity.ok(response);
    }

    @Transactional
    public void updateAverageRatingsAndPricing(){
        restaurantRepository
                .findAll()
                .forEach(restaurant -> {
            restaurant.setRating(calculateAverageRating(restaurant.getRatings()));
            restaurant.setPrices((int)calculateAveragePrice(restaurant));
            restaurant.setCommentsCount(calculateCountOfRatings(restaurant.getRatings()));
            restaurantRepository.save(restaurant);
            restaurantRepository.flush();
        });
    }
    public Set<RestaurantConversationResponse> findRestaurantsByIds(Set<Long> ids) {
        return restaurantRepository.findAllById(ids).stream()
                .map(restaurant -> new RestaurantConversationResponse(restaurant.getRestaurantId(), restaurant.getName()))
                .collect(Collectors.toSet());
    }
}