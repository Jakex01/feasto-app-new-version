package org.example.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStats {
    private Long userId;
    private String userEmail;
    private Integer ordersCount;
    private LocalDateTime lastOrder;
    private Double totalSpent;
}
