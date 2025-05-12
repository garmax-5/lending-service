package com.example.lending_service.service.impl;

import com.example.lending_service.model.LoanActionsLog;
import com.example.lending_service.repository.LoanActionsLogRepository;
import com.example.lending_service.service.LoanActionsLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class LoanActionsLogServiceImpl implements LoanActionsLogService {

    private final LoanActionsLogRepository loanActionsLogRepository;

    @Override
    public List<LoanActionsLog> getLogsByLoanId(Long loanId) {
        return loanActionsLogRepository.findByContractContractId(loanId);
    }
}
