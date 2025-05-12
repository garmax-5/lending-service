package com.example.lending_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class LoanContractDTO {
    private Long contractId;
    private String clientFullName;
    private String employeeFullName;
    private BigDecimal loanAmount;
    private String paymentType;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
}

