package org.restaurant.model;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@Data
public class FoodAdditivePriceEntity {

    private String foodAdditive;
    private Double price;
}
