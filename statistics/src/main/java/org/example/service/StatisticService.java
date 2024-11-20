package org.example.service;

import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregateBase;
import lombok.RequiredArgsConstructor;
import org.example.model.entity.CustomerStats;
import org.example.model.entity.OrderDetail;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticService {


    private final ElasticsearchOperations searchOperations;


    public List<CustomerStats> getTop5CustomersByRestaurantId(Long restaurantId) throws Exception {
        Query query = NativeQuery.builder()
                .withQuery(q -> q.term(t -> t
                        .field("restaurantId")
                        .value(restaurantId)
                ))
                .withAggregation("top_customers", co.elastic.clients.elasticsearch._types.aggregations.Aggregation.of(a -> a
                        .terms(ta -> ta
                                .field("userId")
                                .size(5)
                        )
                        .aggregations("total_price", co.elastic.clients.elasticsearch._types.aggregations.Aggregation.of(sa -> sa
                                .sum(sum -> sum
                                        .field("totalPrice")
                                )
                        ))
                ))
                .build();

        SearchHits<OrderDetail> response = searchOperations.search(query, OrderDetail.class);

        ElasticsearchAggregations aggregations = (ElasticsearchAggregations) response.getAggregations();

        TermsAggregateBase<StringTermsBucket> topCustomers = aggregations
                .aggregationsAsMap()
                .get("top_customers")
                .aggregation()
                .getAggregate()
                .sterms();
        List<StringTermsBucket> userBuckets = topCustomers.buckets().array();
        List<CustomerStats> topCustomersList = userBuckets.stream()
                .map(bucket -> new CustomerStats(
                        Long.valueOf(bucket.key().stringValue()), // userId
                        bucket.aggregations().get("total_price").sum().value() // Total price sum
                ))
                .collect(Collectors.toList());

        return topCustomersList;
    }





}
