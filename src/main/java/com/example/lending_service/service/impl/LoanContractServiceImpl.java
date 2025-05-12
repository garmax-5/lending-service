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


//    @Override
//    public LoanContract createLoan(LoanContract loanContract) {
//        return loanContractRepository.save(loanContract);
//    }

    @Override
    public LoanContractDTO getLoan(Long id) {
        return toDTO(loanContractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found")));
    }

//    @Override
//    public LoanContract createLoan(CreateLoanRequest request) {
//        Client client = clientRepository.findById(request.getClientId())
//                .orElseThrow(() -> new RuntimeException("Client not found"));
//        Employee employee = employeeRepository.findById(request.getEmployeeId())
//                .orElseThrow(() -> new RuntimeException("Employee not found"));
//
//        LoanContract contract = new LoanContract();
//        contract.setClient(client);
//        contract.setEmployee(employee);
//        contract.setLoanAmount(request.getLoanAmount());
//        contract.setStartDate(request.getStartDate());
//        contract.setEndDate(request.getEndDate());
//        contract.setPaymentType(PaymentType.valueOf(request.getPaymentType()));
//        contract.setStatus("active");
//
//        return loanContractRepository.save(contract);
//    }

// Файл: LoanContractServiceImpl.java

    @Override
    public LoanContract createLoan(CreateLoanRequest request, String employeeEmail) {
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
        contract.setStatus("active");

        LoanContract savedContract = loanContractRepository.save(contract);

        InterestRateHistory history = new InterestRateHistory();
        history.setContract(savedContract);
        history.setRate(request.getRate());
        history.setStartDate(LocalDate.now());
        history.setEmployee(employee);

        interestRateHistoryRepository.save(history);

        return savedContract;
    }


    @Override
    public List<LoanContractDTO> getAllLoans() {
        return loanContractRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void calculateSchedule(Long id) {
        LoanContract contract = loanContractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        List<PaymentSchedule> payments = paymentScheduleRepository.findByContractContractId(id);

        // Посчитать сумму уже выплаченного основного долга
        BigDecimal totalPrincipalPaid = payments.stream()
                .filter(PaymentSchedule::isPaid)
                .map(PaymentSchedule::getPrincipalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Удалить неоплаченные платежи
        List<PaymentSchedule> unpaidPayments = payments.stream()
                .filter(p -> !p.isPaid())
                .collect(Collectors.toList());

        paymentScheduleRepository.deleteAll(unpaidPayments);

        // Посчитать остаток долга
        BigDecimal remainingPrincipal = contract.getLoanAmount().subtract(totalPrincipalPaid);
        if (remainingPrincipal.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        // Определить дату следующего платежа (после последнего оплаченного)
        LocalDate firstPaymentDate = payments.stream()
                .filter(PaymentSchedule::isPaid)
                .map(PaymentSchedule::getPaymentDate)
                .max(LocalDate::compareTo)
                .orElse(contract.getStartDate())
                .plusMonths(1);

        // Кол-во месяцев: по числу удалённых платежей или между датами, если их не было
        int remainingMonths = unpaidPayments.size();
        if (remainingMonths == 0) {
            LocalDate tempDate = firstPaymentDate;
            while (tempDate.isBefore(contract.getEndDate())) {
                remainingMonths++;
                tempDate = tempDate.plusMonths(1);
            }
        }

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
                .filter(PaymentSchedule::isPaid)
                .map(PaymentSchedule::getPrincipalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalInterestPaid = payments.stream()
                .filter(PaymentSchedule::isPaid)
                .map(PaymentSchedule::getInterestAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

//        BigDecimal totalPrincipalScheduled = payments.stream()
//                .map(PaymentSchedule::getPrincipalAmount)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);

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
                contract.getStatus(),
                contract.getStartDate(),
                contract.getEndDate()
        );
    }


}
