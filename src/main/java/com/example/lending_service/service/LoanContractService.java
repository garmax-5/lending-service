package com.example.lending_service.service;

import com.example.lending_service.dto.LoanSummaryDTO;
import com.example.lending_service.model.LoanContract;
import java.util.List;

public interface LoanContractService {

    LoanContract createLoan(LoanContract loanContract);

    LoanContract getLoan(Long id);

    List<LoanContract> getAllLoans();

    void calculateSchedule(Long id);

    LoanSummaryDTO getLoanSummary(Long loanId);
}
