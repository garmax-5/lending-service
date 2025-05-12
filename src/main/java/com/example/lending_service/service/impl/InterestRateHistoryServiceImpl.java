package com.example.lending_service.service.impl;

import com.example.lending_service.model.Employee;
import com.example.lending_service.model.InterestRateHistory;
import com.example.lending_service.model.LoanActionsLog;
import com.example.lending_service.model.LoanContract;
import com.example.lending_service.model.PaymentSchedule;
import com.example.lending_service.model.PaymentType;
import com.example.lending_service.repository.EmployeeRepository;
import com.example.lending_service.repository.InterestRateHistoryRepository;
import com.example.lending_service.repository.LoanActionsLogRepository;
import com.example.lending_service.repository.LoanContractRepository;
import com.example.lending_service.repository.PaymentScheduleRepository;
import com.example.lending_service.service.InterestRateHistoryService;
import com.example.lending_service.util.Calculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterestRateHistoryServiceImpl implements InterestRateHistoryService {

    private final InterestRateHistoryRepository interestRateHistoryRepository;
    private final LoanContractRepository loanContractRepository;
    private final EmployeeRepository employeeRepository;
    private final LoanActionsLogRepository loanActionsLogRepository;
    private final PaymentScheduleRepository paymentScheduleRepository;

    @Override
    public void updateInterestRate(Long loanId, BigDecimal newRate, LocalDate startDate, Long employeeId) {
        LoanContract contract = loanContractRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan contract not found"));

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Сохраняем новую запись в истории ставок
        InterestRateHistory rateHistory = InterestRateHistory.builder()
                .contract(contract)
                .rate(newRate)
                .startDate(startDate)
                .employee(employee)
                .build();
        interestRateHistoryRepository.save(rateHistory);

        // Получаем все платежи
        List<PaymentSchedule> allPayments = paymentScheduleRepository.findByContractContractId(loanId);

        // Считаем погашенную часть основного долга
        BigDecimal totalPrincipalPaid = allPayments.stream()
                .filter(PaymentSchedule::isPaid)
                .map(PaymentSchedule::getPrincipalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Удаляем ТОЛЬКО неоплаченные платежи с датой >= startDate
        List<PaymentSchedule> toDelete = allPayments.stream()
                .filter(p -> !p.isPaid() && !p.getPaymentDate().isBefore(startDate))
                .collect(Collectors.toList());

        paymentScheduleRepository.deleteAll(toDelete);

        // Остаток основного долга
        BigDecimal remainingPrincipal = contract.getLoanAmount().subtract(totalPrincipalPaid);
        if (remainingPrincipal.compareTo(BigDecimal.ZERO) <= 0) return;

        // Определяем количество месяцев по оставшимся датам
        int remainingMonths = toDelete.size();
        if (remainingMonths == 0) {
            LocalDate cursor = startDate;
            LocalDate end = contract.getEndDate();
            while (cursor.isBefore(end)) {
                remainingMonths++;
                cursor = cursor.plusMonths(1);
            }
        }

        // Перерасчёт графика с новой ставкой
        List<PaymentSchedule> newPayments = (contract.getPaymentType() == PaymentType.ANNUITY)
                ? Calculator.calculateAnnuitySchedule(contract, remainingPrincipal, newRate, remainingMonths, startDate)
                : Calculator.calculateDifferentiatedSchedule(contract, remainingPrincipal, newRate, remainingMonths, startDate);

        paymentScheduleRepository.saveAll(newPayments);

        // Логирование действия
        LoanActionsLog log = LoanActionsLog.builder()
                .contract(contract)
                .employee(employee)
                .actionType("RATE_CHANGE")
                .description("Ставка изменена на " + newRate + "% с даты " + startDate)
                .build();
        loanActionsLogRepository.save(log);
    }


    @Override
    public List<InterestRateHistory> getRateHistoryByLoanId(Long loanId) {
        return interestRateHistoryRepository.findByContractContractId(loanId);
    }

    @Override
    public BigDecimal getCurrentRateForContract(Long contractId) {
        return interestRateHistoryRepository.findByContractContractId(contractId).stream()
                .max(Comparator.comparing(InterestRateHistory::getStartDate))
                .orElseThrow(() -> new RuntimeException("No rate history for contract: " + contractId))
                .getRate();
    }
}
