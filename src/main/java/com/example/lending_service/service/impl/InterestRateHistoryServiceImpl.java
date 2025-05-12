package com.example.lending_service.service.impl;

import com.example.lending_service.model.Employee;
import com.example.lending_service.model.InterestRateHistory;
import com.example.lending_service.model.LoanActionsLog;
import com.example.lending_service.model.LoanContract;
import com.example.lending_service.repository.EmployeeRepository;
import com.example.lending_service.repository.InterestRateHistoryRepository;
import com.example.lending_service.repository.LoanActionsLogRepository;
import com.example.lending_service.repository.LoanContractRepository;
import com.example.lending_service.service.InterestRateHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
class InterestRateHistoryServiceImpl implements InterestRateHistoryService {

    private final InterestRateHistoryRepository interestRateHistoryRepository;
    private final LoanContractRepository loanContractRepository;
    private final EmployeeRepository employeeRepository;
    private final LoanActionsLogRepository loanActionsLogRepository;

    @Override
    public List<InterestRateHistory> getRateHistoryByLoanId(Long loanId) {
        return interestRateHistoryRepository.findByContractContractId(loanId);
    }

    @Override
    public void updateInterestRate(Long loanId, BigDecimal newRate, LocalDate startDate, Long employeeId) {
        LoanContract contract = loanContractRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan contract not found"));
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Создание новой записи в истории ставок
        InterestRateHistory rateHistory = InterestRateHistory.builder()
                .contract(contract)
                .rate(newRate)
                .startDate(startDate)
                .employee(employee)
                .build();

        interestRateHistoryRepository.save(rateHistory);

        // Пересчитать график платежей с новой ставкой (заглушка)

        // Логирование действия
        LoanActionsLog log = LoanActionsLog.builder()
                .contract(contract)
                .employee(employee)
                .actionType("RATE_CHANGE")
                .description("Ставка изменена на " + newRate + "% с даты " + startDate)
                .build();

        loanActionsLogRepository.save(log);
    }

}
