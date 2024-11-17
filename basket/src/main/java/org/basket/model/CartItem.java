package org.basket.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@AllArgsConstructor
@Data
public class CartItem implements Serializable {
    private String name;
    private Integer quantity;
    private String size;
    private Map<String, Double> additives;
}
