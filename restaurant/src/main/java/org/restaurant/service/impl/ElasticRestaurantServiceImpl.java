package org.restaurant.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.AllArgsConstructor;
import org.restaurant.exceptions.RestaurantSearchException;
import org.restaurant.model.RestaurantEntity;
import org.restaurant.model.elasticsearch.ElasticRestaurant;
import org.restaurant.repository.elasticsearch.ElasticRestaurantRepository;
import org.restaurant.request.FilterRestaurant;
import org.restaurant.service.ElasticRestaurantService;
import org.restaurant.util.ElasticSearchQueryHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ElasticRestaurantServiceImpl implements ElasticRestaurantService {
    private final ElasticsearchClient elasticsearchClient;
    private final ElasticRestaurantRepository elasticRepository;
    @Override
    public void saveRestaurant(RestaurantEntity restaurantEntity) {
        ElasticRestaurant elasticRestaurant = ElasticRestaurant.builder()
                .id(String.valueOf(restaurantEntity.getRestaurantId()))
                .name(restaurantEntity.getName())
                .description(restaurantEntity.getDescription())
                .foodType(restaurantEntity.getFoodType())
                .image(restaurantEntity.getImage())
                .openingHours(restaurantEntity.getOpeningHours())
                .prices(restaurantEntity.getPrices())
                .build();
        elasticRepository.save(elasticRestaurant);
    }

    @Override
    public Page<ElasticRestaurant> filterRestaurants(FilterRestaurant filterRestaurant, Pageable pageable) throws RestaurantSearchException {
        try {
            int from = (int) pageable.getOffset();
            SearchResponse<ElasticRestaurant> response = elasticsearchClient.search(s -> s
                            .index("restaurants")
                            .from(from)
                            .size(pageable.getPageSize())
                            .query(q -> q.bool(b -> ElasticSearchQueryHelper.buildQuery(b, filterRestaurant)))
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

    @Override
    public void deleteById(String restaurantId) {
        elasticRepository.deleteById(restaurantId);
    }

}
