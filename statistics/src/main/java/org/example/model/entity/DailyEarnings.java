package org.example.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DailyEarnings {
    private LocalDate date;
    private Double totalEarnings;

}
