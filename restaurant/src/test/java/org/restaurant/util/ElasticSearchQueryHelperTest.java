package org.restaurant.util;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import org.junit.jupiter.api.BeforeEach;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ElasticSearchQueryHelperTest {
    @Mock
    private BoolQuery.Builder boolQueryBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(boolQueryBuilder.filter(any(Function.class))).thenReturn(boolQueryBuilder);
        when(boolQueryBuilder.filter(any(Function.class))).thenReturn(boolQueryBuilder);
    }


}