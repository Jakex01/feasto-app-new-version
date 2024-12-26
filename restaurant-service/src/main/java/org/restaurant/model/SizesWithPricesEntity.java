package org.restaurant.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@Data
public class SizesWithPricesEntity {
    private String size;
    private Double price;
}
