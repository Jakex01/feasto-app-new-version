package org.restaurant.util;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.json.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.restaurant.request.FilterRestaurant;

import java.util.List;

@Slf4j
public final class ElasticSearchQueryHelper {
    private static final Integer BOUND_15 = 15;
    private static final Integer BOUND_30 = 30;
    private static final Integer BOUND_45 = 45;
    private static final Integer BOUND_150 = 150;

    private ElasticSearchQueryHelper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static BoolQuery buildQuery(FilterRestaurant filter, List<Long> likedRestaurantIds) {
        BoolQuery.Builder builder = new BoolQuery.Builder();
        if (filter.restaurantName() != null && !filter.restaurantName().isBlank()) {
            builder.must(m -> m.matchPhrasePrefix(mt -> mt.field("name").query(filter.restaurantName())));
            log.info("Added wildcard query for name: {}", filter.restaurantName());
        }

        // Food type query
        if (filter.foodType() != null && !filter.foodType().isBlank()) {
            builder.filter(f -> f.term(t -> t.field("foodType").value(filter.foodType().toLowerCase())));
            log.info("Added term query for foodType: {}", filter.foodType());
        }

        // Rating query
        if (filter.rating() != null && filter.rating() != 0) {
            double upperLimit = (Math.floor(filter.rating()) + 0.5 > filter.rating())
                    ? Math.floor(filter.rating()) + 0.5
                    : Math.ceil(filter.rating());
            builder.filter(f -> f.range(r -> r.field("rating")
                    .gte(JsonData.of(filter.rating()))
                    .lte(JsonData.of(upperLimit))));
            log.info("Added range query for rating: {} - {}", filter.rating(), upperLimit);
        }
        if (filter.onlyLiked() != null && filter.onlyLiked() && likedRestaurantIds != null && !likedRestaurantIds.isEmpty()) {
            List<FieldValue> likedRestaurantFieldValues = likedRestaurantIds.stream()
                    .map(FieldValue::of)
                    .toList();

            builder.filter(f -> f.terms(t -> t.field("id").terms(terms -> terms.value(likedRestaurantFieldValues))));
            log.info("Added terms query for liked restaurant IDs: {}", likedRestaurantIds);
        }


        // Price query
        if (filter.priceRange() != null && filter.priceRange() != 0.0) {
            int priceKey = filter.priceRange().intValue();
            switch (priceKey) {
                case 15:
                    builder.filter(f -> f.range(r -> r.field("prices")
                            .gte(JsonData.of(BOUND_15))
                            .lte(JsonData.of(BOUND_30))));
                    log.info("Added range query for prices: {} - {}", BOUND_15, BOUND_30);
                    break;
                case 30:
                    builder.filter(f -> f.range(r -> r.field("prices")
                            .gte(JsonData.of(BOUND_30))
                            .lte(JsonData.of(BOUND_45))));
                    log.info("Added range query for prices: {} - {}", BOUND_30, BOUND_45);
                    break;
                case 45:
                    builder.filter(f -> f.range(r -> r.field("prices")
                            .gte(JsonData.of(BOUND_45))
                            .lte(JsonData.of(BOUND_150))));
                    log.info("Added range query for prices: {} - {}", BOUND_45, BOUND_150);
                    break;
            }
        }

        return builder.build();
    }
}
