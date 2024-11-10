package org.restaurant.util;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.json.JsonData;
import org.restaurant.request.FilterRestaurant;

public final class ElasticSearchQueryHelper {

    private static final Integer BOUND_15 = 15;
    private static final Integer BOUND_30 = 30;
    private static final Integer BOUND_45 = 45;
    private static final Integer BOUND_150 = 150;

    private ElasticSearchQueryHelper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    public static BoolQuery.Builder buildQuery(BoolQuery.Builder builder, FilterRestaurant filterRestaurant) {
        addNameQuery(builder, filterRestaurant.restaurantName());
        addFoodTypeQuery(builder, filterRestaurant.foodType());
        addRatingQuery(builder, filterRestaurant.rating());
        addPriceQuery(builder, filterRestaurant.priceRange());
        return builder;

    }
    private static void addNameQuery(BoolQuery.Builder builder, String name) {
        if (name != null && !name.isBlank()) {
            builder.must(m -> m.match(mt -> mt.field("name").query(name)));
        }
    }
    private static void addFoodTypeQuery(BoolQuery.Builder builder, String foodType) {
        if (foodType != null && !foodType.isBlank()) {
            builder.filter(f -> f.term(t -> t.field("foodType").value(foodType.toLowerCase())));
        }
    }
    private static void addRatingQuery(BoolQuery.Builder builder, Double rating) {
        if (rating != null && rating != 0) {
            double upperLimit = (Math.floor(rating) + 0.5 > rating) ? Math.floor(rating) + 0.5 : Math.ceil(rating);
            builder.filter(f -> f.range(r -> r.field("rating")
                    .gte(JsonData.of(rating))
                    .lte(JsonData.of(upperLimit))));
        }
    }
    private static void addPriceQuery(BoolQuery.Builder builder, Double price) {
        if (price == null || price == 0.0) {
            return;
        }
        int priceKey = price.intValue();
        switch (priceKey) {
            case 15:
                builder.filter(f -> f.range(r -> r.field("prices")
                        .gte(JsonData.of(BOUND_15))
                        .lte(JsonData.of(BOUND_30))));
                break;
            case 30:
                builder.filter(f -> f.range(r -> r.field("prices")
                        .gte(JsonData.of(BOUND_30))
                        .lte(JsonData.of(BOUND_45))));
                break;
            case 45:
                builder.filter(f -> f.range(r -> r.field("prices")
                        .gte(JsonData.of(BOUND_45))
                        .lte(JsonData.of(BOUND_150))));
                break;
        }
    }
}
