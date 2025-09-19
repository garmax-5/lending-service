package com.example.lending_service.service;

import com.example.lending_service.dto.LoanActionLogDTO;
import com.example.lending_service.model.LoanActionsLog;

import java.util.List;

public interface LoanActionsLogService {
    List<LoanActionLogDTO> getLogsByLoanId(Long loanId);
}

