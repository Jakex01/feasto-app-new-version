package org.example.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerDetailsDto {
    private Long userId;
    private String name;
    private String email;
    private Double totalSpent;
}
