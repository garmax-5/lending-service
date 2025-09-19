package com.example.lending_service.service;

import com.example.lending_service.dto.InterestRateHistoryDTO;
import com.example.lending_service.model.InterestRateHistory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface InterestRateHistoryService {
    List<InterestRateHistoryDTO> getRateHistoryByLoanId(Long loanId);
    BigDecimal getCurrentRateForContract(Long contractId);
    void updateInterestRate(Long loanId, BigDecimal newRate, LocalDate startDate, Long employeeId);
}

