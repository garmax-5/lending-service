package com.example.lending_service.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateRateRequest {

    @NotNull(message = "New rate is required")
    @DecimalMin(value = "0.1", message = "Rate must be at least 0.1")
    private BigDecimal newRate;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;

//    @NotNull(message = "Employee ID is required")
//    private Long employeeId;
}
