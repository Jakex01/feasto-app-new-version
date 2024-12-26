package org.restaurant.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.restaurant.model.enums.FoodCategory;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@RequiredArgsConstructor
public class MenuItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuItemId;

    private String name;
    private String description;
    private boolean available;
    @Enumerated(EnumType.STRING)
    private FoodCategory foodCategory;

    @ElementCollection
    @CollectionTable(name = "menu_item_food_additives", joinColumns = @JoinColumn(name = "menu_item_id"))
    private List<FoodAdditivePriceEntity> foodAdditivePrices = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "menu_item_sizes", joinColumns = @JoinColumn(name = "menu_item_id"))
    private List<SizesWithPricesEntity> sizesWithPrices = new ArrayList<>();

    private Double lowestPrice;



    @PrePersist
    @PreUpdate
    private void updateLowestPrice() {
        this.lowestPrice = this.sizesWithPrices.stream()
                .map(SizesWithPricesEntity::getPrice)
                .min(Double::compare)
                .orElseThrow(() -> new IllegalStateException("Map with prices can't be empty"));
    }


}
