package com.example.lending_service.service.impl;

import com.example.lending_service.dto.LoanSummaryDTO;
import com.example.lending_service.model.LoanContract;
import com.example.lending_service.model.PaymentSchedule;
import com.example.lending_service.repository.LoanContractRepository;
import com.example.lending_service.repository.PaymentScheduleRepository;
import com.example.lending_service.service.LoanContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanContractServiceImpl implements LoanContractService {

    private final LoanContractRepository loanContractRepository;
    private final PaymentScheduleRepository paymentScheduleRepository;

    @Override
    public LoanContract createLoan(LoanContract loanContract) {
        return loanContractRepository.save(loanContract);
    }

    @Override
    public LoanContract getLoan(Long id) {
        return loanContractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
    }

    @Override
    public List<LoanContract> getAllLoans() {
        return loanContractRepository.findAll();
    }

    @Override
    public void calculateSchedule(Long id) {
        //Реализация расчета графика погашения
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

        BigDecimal totalPrincipalScheduled = payments.stream()
                .map(PaymentSchedule::getPrincipalAmount)
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
}
