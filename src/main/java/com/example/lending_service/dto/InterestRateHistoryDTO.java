package com.example.lending_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterestRateHistoryDTO {
    private Long id;
    private BigDecimal rate;
    private LocalDate startDate;
    private String employeeName;
}
