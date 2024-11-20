package org.example.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long restaurantId;
    private Long orderId;
    private Long userId;
    private LocalDateTime orderDate;
    private LocalDateTime finishedDate;
    private Double totalPrice;
    private String orderStatus;
    private String deliveryOption;
}

