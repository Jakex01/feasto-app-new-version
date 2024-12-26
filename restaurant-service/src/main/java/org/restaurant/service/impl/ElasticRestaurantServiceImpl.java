package org.restaurant.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.restaurant.config.UrlConfig;
import org.restaurant.exceptions.RestaurantSearchException;
import org.restaurant.exceptions.UserNotFoundException;
import org.restaurant.model.FavoriteRestaurantEntity;
import org.restaurant.model.RestaurantEntity;
import org.restaurant.model.elasticsearch.ElasticRestaurant;
import org.restaurant.repository.UserFavoriteRestaurantRepository;
import org.restaurant.repository.elasticsearch.ElasticRestaurantRepository;
import org.restaurant.request.FilterRestaurant;
import org.restaurant.service.ElasticRestaurantService;
import org.restaurant.util.ElasticSearchQueryHelper;
import org.restaurant.util.JwtUtil;
import org.restaurant.util.UserDetailsClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
@Slf4j
public class ElasticRestaurantServiceImpl implements ElasticRestaurantService {
    private final ElasticsearchClient elasticsearchClient;
    private final ElasticRestaurantRepository elasticRepository;
    private final UserFavoriteRestaurantRepository userFavoriteRestaurantRepository;
    private final UrlConfig urlConfig;
    private final UserDetailsClient userDetailsClient;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void saveRestaurant(RestaurantEntity restaurantEntity) {
        ElasticRestaurant elasticRestaurant = ElasticRestaurant.builder()
                .id(String.valueOf(restaurantEntity.getRestaurantId()))
                .name(restaurantEntity.getName())
                .description(restaurantEntity.getDescription())
                .foodType(restaurantEntity.getFoodType())
                .openingHours(restaurantEntity.getOpeningHours())
                .prices(restaurantEntity.getPrices())
                .build();
        elasticRepository.save(elasticRestaurant);
    }

    @Override
    public ResponseEntity<Page<ElasticRestaurant>> getAllRestaurants(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ElasticRestaurant> restaurantPage = elasticRepository.findAll(pageable);
        return ResponseEntity.ok(restaurantPage);
    }

    @Override
    public Page<ElasticRestaurant> filterRestaurants(FilterRestaurant filterRestaurant, String token, Pageable pageable) throws RestaurantSearchException {
        try {
            int from = (int) pageable.getOffset();

            List<Long> likedRestaurantIds;
            if (filterRestaurant.onlyLiked()) {

                String userEmail = userDetailsClient
                        .fetchData(urlConfig.getUserService(), String.class, JwtUtil.extractToken(token)).block();

                if (userEmail == null) {
                    throw new UserNotFoundException("User not present");
                }

                likedRestaurantIds = userFavoriteRestaurantRepository
                        .findAllByUserEmail(userEmail) // Wyszukaj ulubione restauracje użytkownika
                        .stream()
                        .map(FavoriteRestaurantEntity::getRestaurantId)
                        .toList();
            } else {
                likedRestaurantIds = new ArrayList<>();
            }

            SearchResponse<ElasticRestaurant> response = elasticsearchClient.search(s -> s
                            .index("restaurants")
                            .from(from)
                            .size(pageable.getPageSize())
                            .query(q -> q.bool(ElasticSearchQueryHelper.buildQuery(filterRestaurant, likedRestaurantIds)))
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
    public static String getQueryAsString(BoolQuery.Builder builder) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(builder.build()._toQuery());
        } catch (Exception e) {
            log.error("Error serializing query to JSON", e);
            return "{}";
        }
    }

    @Override
    public void deleteById(String restaurantId) {
        elasticRepository.deleteById(restaurantId);
    }

    @Override
    public void updateImageUrl(String restaurantId, String imageUrl) {
        ElasticRestaurant elasticRestaurant = elasticRepository.findById(restaurantId).orElseThrow();
        elasticRestaurant.setImageUrl(imageUrl);
        elasticRepository.save(elasticRestaurant);
    }

}
