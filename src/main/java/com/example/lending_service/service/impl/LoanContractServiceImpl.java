package com.example.lending_service.service.impl;

import com.example.lending_service.dto.CreateLoanRequest;
import com.example.lending_service.dto.LoanContractDTO;
import com.example.lending_service.dto.LoanSummaryDTO;
import com.example.lending_service.model.*;
import com.example.lending_service.repository.*;
import com.example.lending_service.service.InterestRateHistoryService;
import com.example.lending_service.service.LoanContractService;
import com.example.lending_service.util.Calculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanContractServiceImpl implements LoanContractService {

    private final LoanContractRepository loanContractRepository;
    private final PaymentScheduleRepository paymentScheduleRepository;
    private final InterestRateHistoryRepository interestRateHistoryRepository;
    private final InterestRateHistoryService interestRateHistoryService;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public LoanContractDTO getLoan(Long id) {
        return toDTO(loanContractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found")));
    }

    @Override
    @Transactional
    public LoanContractDTO createLoan(CreateLoanRequest request, String employeeEmail) {
        Client client = clientRepository.findByFullNameIgnoreCase(request.getClientFullName())
                .orElseThrow(() -> new RuntimeException("Клиент с ФИО '" + request.getClientFullName() + "' не найден"));

        Employee employee = employeeRepository.findByEmailIgnoreCase(employeeEmail)
                .orElseThrow(() -> new RuntimeException("Сотрудник с email '" + employeeEmail + "' не найден"));

        LoanContract contract = new LoanContract();
        contract.setClient(client);
        contract.setEmployee(employee);
        contract.setLoanAmount(request.getLoanAmount());
        contract.setStartDate(request.getStartDate());
        contract.setEndDate(request.getEndDate());
        contract.setPaymentType(request.getPaymentType());
        contract.setStatus(ContractStatus.ACTIVE);

        LoanContract savedContract = loanContractRepository.save(contract);

        InterestRateHistory history = new InterestRateHistory();
        history.setContract(savedContract);
        history.setRate(request.getRate());
        history.setStartDate(LocalDate.now());
        history.setEmployee(employee);
        interestRateHistoryRepository.save(history);

        int months = Calculator.calculateRemainingMonths(contract.getStartDate(), contract.getEndDate());
        List<PaymentSchedule> schedule = (contract.getPaymentType() == PaymentType.ANNUITY)
                ? Calculator.calculateAnnuitySchedule(savedContract, request.getLoanAmount(), request.getRate(), months, contract.getStartDate())
                : Calculator.calculateDifferentiatedSchedule(savedContract, request.getLoanAmount(), request.getRate(), months, contract.getStartDate());

        paymentScheduleRepository.saveAll(schedule);

        return toDTO(savedContract);
    }

    @Override
    public List<LoanContractDTO> getAllLoans() {
        return loanContractRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void calculateSchedule(Long id) {
        LoanContract contract = loanContractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        List<PaymentSchedule> payments = paymentScheduleRepository.findByContractContractId(id);

        List<PaymentSchedule> unpaidPayments = payments.stream()
                .filter(p -> !p.getIsPaid())
                .collect(Collectors.toList());

        paymentScheduleRepository.deleteAll(unpaidPayments);

        BigDecimal remainingPrincipal = unpaidPayments.stream()
                .map(PaymentSchedule::getPrincipalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (remainingPrincipal.compareTo(BigDecimal.ZERO) <= 0) return;

        LocalDate firstPaymentDate = payments.stream()
                .filter(PaymentSchedule::getIsPaid)
                .map(PaymentSchedule::getPaymentDate)
                .max(LocalDate::compareTo)
                .orElse(contract.getStartDate())
                .plusMonths(1);

        int remainingMonths = Calculator.calculateRemainingMonths(firstPaymentDate, contract.getEndDate());
        BigDecimal annualRate = interestRateHistoryService.getCurrentRateForContract(contract.getContractId());

        List<PaymentSchedule> newPayments = (contract.getPaymentType() == PaymentType.ANNUITY)
                ? Calculator.calculateAnnuitySchedule(contract, remainingPrincipal, annualRate, remainingMonths, firstPaymentDate)
                : Calculator.calculateDifferentiatedSchedule(contract, remainingPrincipal, annualRate, remainingMonths, firstPaymentDate);

        paymentScheduleRepository.saveAll(newPayments);
    }

    @Override
    public LoanSummaryDTO getLoanSummary(Long loanId) {
        LoanContract contract = loanContractRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan contract not found"));

        List<PaymentSchedule> payments = paymentScheduleRepository.findByContractContractId(loanId);

        BigDecimal totalPrincipalPaid = payments.stream()
                .filter(PaymentSchedule::getIsPaid)
                .map(PaymentSchedule::getPrincipalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalInterestPaid = payments.stream()
                .filter(PaymentSchedule::getIsPaid)
                .map(PaymentSchedule::getInterestAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalInterestScheduled = payments.stream()
                .map(PaymentSchedule::getInterestAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remainingPrincipal = contract.getLoanAmount().subtract(totalPrincipalPaid);

        return new LoanSummaryDTO(
                totalPrincipalPaid,
                totalInterestPaid,
                remainingPrincipal,
                totalInterestScheduled
        );
    }

    private LoanContractDTO toDTO(LoanContract contract) {
        return new LoanContractDTO(
                contract.getContractId(),
                contract.getClient().getFullName(),
                contract.getEmployee().getFullName(),
                contract.getLoanAmount(),
                contract.getPaymentType().name(),
                contract.getStatus().name(),
                contract.getStartDate(),
                contract.getEndDate()
        );
    }
}
