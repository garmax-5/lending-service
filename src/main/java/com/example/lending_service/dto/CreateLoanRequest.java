package com.example.lending_service.dto;

import com.example.lending_service.model.PaymentType;
import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateLoanRequest {

    @NotBlank(message = "Client full name is required")
    private String clientFullName;

//    @NotBlank(message = "Employee full name is required")
//    private String employeeFullName;

    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "0.01", message = "Loan amount must be greater than 0")
    private BigDecimal loanAmount;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Payment type is required")
    private PaymentType paymentType;

    @NotNull(message = "Rate is required")
    @DecimalMin(value = "0.01", message = "Rate must be greater than 0")
    private BigDecimal rate;
}