package com.example.lending_service.service;

import com.example.lending_service.model.LoanActionsLog;

import java.util.List;

public interface LoanActionsLogService {
    List<LoanActionsLog> getLogsByLoanId(Long loanId);
}
