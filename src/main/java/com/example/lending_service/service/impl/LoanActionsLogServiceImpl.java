package com.example.lending_service.service.impl;

import com.example.lending_service.dto.LoanActionLogDTO;
import com.example.lending_service.model.LoanActionsLog;
import com.example.lending_service.repository.LoanActionsLogRepository;
import com.example.lending_service.service.LoanActionsLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanActionsLogServiceImpl implements LoanActionsLogService {

    private final LoanActionsLogRepository loanActionsLogRepository;

    @Override
    public List<LoanActionLogDTO> getLogsByLoanId(Long loanId) {
        return loanActionsLogRepository.findByContractContractIdOrderByActionTimeDesc(loanId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList()); // вместо toList() для совместимости с Java 11
    }

    private LoanActionLogDTO toDTO(LoanActionsLog log) {
        return new LoanActionLogDTO(
                log.getLogId(),
                log.getContract() != null ? log.getContract().getContractId() : null,
                log.getEmployee() != null ? log.getEmployee().getFullName() : null,
                log.getActionType(),
                log.getDescription(),
                log.getActionTime()
        );
    }
}



