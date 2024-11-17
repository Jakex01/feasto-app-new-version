package org.basket.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Map;

@RedisHash("Cart")
@Data
@AllArgsConstructor
public class Cart implements Serializable {

    @Id
    private String userEmail;
    private Map<String, CartItem> items;

    public void addItem(String productId, CartItem item) {
        this.items.put(productId, item);
    }

    public void removeItem(String productId) {
        this.items.remove(productId);
    }

}