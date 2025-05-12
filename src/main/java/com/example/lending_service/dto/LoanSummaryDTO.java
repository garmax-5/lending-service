package com.example.lending_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class LoanSummaryDTO {
    private BigDecimal totalPrincipalPaid;  // Сумма погашенного основного долга
    private BigDecimal totalInterestPaid;   // Сумма погашенных процентов
    private BigDecimal remainingPrincipal;  // Остаток основного долга
    private BigDecimal totalOverpayment;    // Общая переплата по кредиту
}

