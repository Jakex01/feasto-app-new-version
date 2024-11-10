package org.restaurant.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.restaurant.model.elasticsearch.ElasticRestaurant;
import org.restaurant.repository.elasticsearch.ElasticRestaurantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    private static final int PAGE_INDEX_1 = 1;
    private static final int PAGE_SIZE_10 = 10;
    @InjectMocks
    private RestaurantService restaurantService;
    @Mock
    private ElasticRestaurantRepository elasticRepository;

    @Test
     void testGetAllRestaurantsReturnsCorrectPage() {
        // Arrange
        PageRequest pageable = PageRequest.of(PAGE_INDEX_1, PAGE_SIZE_10);
        List<ElasticRestaurant> restaurants = Arrays.asList(new ElasticRestaurant(), new ElasticRestaurant());
        Page<ElasticRestaurant> restaurantPage = new PageImpl<>(restaurants, pageable, restaurants.size());

        when(elasticRepository.findAll(pageable)).thenReturn(restaurantPage);

        // Act
        ResponseEntity<Page<ElasticRestaurant>> response = restaurantService.getAllRestaurants(PAGE_INDEX_1, PAGE_SIZE_10);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().getContent().size());
        assertEquals(restaurantPage, response.getBody());
    }

}