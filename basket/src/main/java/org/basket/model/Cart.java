package org.basket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@RedisHash("Cart")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart implements Serializable {

    @Id
    private String userEmail;
    private Map<Long, CartItem> items = new HashMap<>();

    public void addItem(Long menuItemId, CartItem item) {
        if (this.items == null) {
            this.items = new HashMap<>();
        }
        this.items.put(menuItemId, item);
    }

    public void removeItem(Long menuItemId) {
        if (this.items != null) {
            this.items.remove(menuItemId);
        }
    }

}