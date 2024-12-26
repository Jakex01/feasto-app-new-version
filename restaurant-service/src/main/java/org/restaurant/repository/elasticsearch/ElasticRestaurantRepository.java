package org.restaurant.repository.elasticsearch;

import org.restaurant.model.elasticsearch.ElasticRestaurant;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticRestaurantRepository extends ElasticsearchRepository<ElasticRestaurant, String> {
    // You can define custom search methods here
}