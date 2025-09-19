package com.example.lending_service.service;

import com.example.lending_service.dto.CreateLoanRequest;
import com.example.lending_service.dto.LoanContractDTO;
import com.example.lending_service.dto.LoanSummaryDTO;
import com.example.lending_service.model.LoanContract;
import java.util.List;

public interface LoanContractService {
    LoanContractDTO createLoan(CreateLoanRequest request, String employeeEmail);
    LoanContractDTO getLoan(Long id);
    List<LoanContractDTO> getAllLoans();
    void calculateSchedule(Long id);
    LoanSummaryDTO getLoanSummary(Long loanId);
}
