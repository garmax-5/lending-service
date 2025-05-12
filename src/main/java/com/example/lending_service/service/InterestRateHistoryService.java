package com.example.lending_service.service;

import com.example.lending_service.model.InterestRateHistory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface InterestRateHistoryService {
    List<InterestRateHistory> getRateHistoryByLoanId(Long loanId);

    void updateInterestRate(Long loanId, BigDecimal newRate, LocalDate startDate, Long employeeId);
}
