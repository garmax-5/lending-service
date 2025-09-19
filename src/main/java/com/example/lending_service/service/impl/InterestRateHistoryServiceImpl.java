package com.example.lending_service.service.impl;

import com.example.lending_service.dto.InterestRateHistoryDTO;
import com.example.lending_service.model.*;
import com.example.lending_service.repository.*;
import com.example.lending_service.service.InterestRateHistoryService;
import com.example.lending_service.util.Calculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterestRateHistoryServiceImpl implements InterestRateHistoryService {

    private final InterestRateHistoryRepository interestRateHistoryRepository;
    private final LoanContractRepository loanContractRepository;
    private final EmployeeRepository employeeRepository;
    private final LoanActionsLogRepository loanActionsLogRepository;
    private final PaymentScheduleRepository paymentScheduleRepository;

    @Override
    @Transactional
    public void updateInterestRate(Long loanId, BigDecimal newRate, LocalDate startDate, Long employeeId) {
        LoanContract contract = loanContractRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan contract not found"));

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        InterestRateHistory rateHistory = InterestRateHistory.builder()
                .contract(contract)
                .rate(newRate)
                .startDate(startDate)
                .employee(employee)
                .build();
        interestRateHistoryRepository.save(rateHistory);

        // Используем существующий метод
        List<PaymentSchedule> allPayments = paymentScheduleRepository
                .findByContractContractIdOrderByPaymentDateAsc(loanId);

        List<PaymentSchedule> toDelete = allPayments.stream()
                .filter(p -> !p.getIsPaid() && !p.getPaymentDate().isBefore(startDate))
                .collect(Collectors.toList());
        paymentScheduleRepository.deleteAll(toDelete);

        BigDecimal remainingPrincipal = toDelete.stream()
                .map(PaymentSchedule::getPrincipalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Rate changed for contract {}: newRate={}, from {}, remainingPrincipal={}",
                loanId, newRate, startDate, remainingPrincipal);

        if (remainingPrincipal.compareTo(BigDecimal.ZERO) <= 0) return;

        int remainingMonths = Calculator.calculateRemainingMonths(startDate, contract.getEndDate());
        log.info("Recalculating schedule for {} months", remainingMonths);

        List<PaymentSchedule> newPayments = (contract.getPaymentType() == PaymentType.ANNUITY)
                ? Calculator.calculateAnnuitySchedule(contract, remainingPrincipal, newRate, remainingMonths, startDate)
                : Calculator.calculateDifferentiatedSchedule(contract, remainingPrincipal, newRate, remainingMonths, startDate);

        paymentScheduleRepository.saveAll(newPayments);

        loanActionsLogRepository.save(LoanActionsLog.builder()
                .contract(contract)
                .employee(employee)
                .actionType("RATE_CHANGE")
                .description("Ставка изменена на " + newRate + "% с " + startDate)
                .build());

        log.info("Schedule successfully recalculated: {} new payments created", newPayments.size());
    }

    @Override
    public List<InterestRateHistoryDTO> getRateHistoryByLoanId(Long loanId) {
        return interestRateHistoryRepository.findByContractContractIdOrderByStartDateAsc(loanId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getCurrentRateForContract(Long contractId) {
        return interestRateHistoryRepository.findByContractContractIdOrderByStartDateAsc(contractId).stream()
                .max(Comparator.comparing(InterestRateHistory::getStartDate))
                .orElseThrow(() -> new RuntimeException("No rate history for contract: " + contractId))
                .getRate();
    }

    private InterestRateHistoryDTO toDTO(InterestRateHistory entity) {
        return new InterestRateHistoryDTO(
                entity.getRateId(),
                entity.getRate(),
                entity.getStartDate(),
                entity.getEmployee() != null ? entity.getEmployee().getFullName() : null
        );
    }
}


