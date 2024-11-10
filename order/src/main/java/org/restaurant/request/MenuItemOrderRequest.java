package org.restaurant.request;

import java.util.Map;

public record MenuItemOrderRequest(
     String name,

     String description,

     boolean available,

     String category,
     int quantity,
     Map<String, Double> foodAdditivePrices,
     Double selectedPrice,
     String selectedSize,
     Double totalItemPrice,
     String note
) {
}
