package org.restaurant.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.restaurant.repository.elasticsearch.ElasticRestaurantRepository;
import org.restaurant.service.impl.RestaurantServiceImpl;


@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
class RestaurantServiceImplTest {

    private static final int PAGE_INDEX_1 = 1;
    private static final int PAGE_SIZE_10 = 10;
    @InjectMocks
    private RestaurantServiceImpl restaurantServiceImpl;
    @Mock
    private ElasticRestaurantRepository elasticRepository;


}