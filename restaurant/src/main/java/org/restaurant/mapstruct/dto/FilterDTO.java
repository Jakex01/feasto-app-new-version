package org.restaurant.mapstruct.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterDTO {
    private String foodType;
    private double rating;
    private String price;
    private boolean sort;

}
