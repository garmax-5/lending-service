package com.example.lending_service.service.impl;

import com.example.lending_service.dto.PaymentDTO;
import com.example.lending_service.model.PaymentSchedule;
import com.example.lending_service.repository.PaymentScheduleRepository;
import com.example.lending_service.service.PaymentScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentScheduleServiceImpl implements PaymentScheduleService {

    private final PaymentScheduleRepository paymentScheduleRepository;

    @Override
    public List<PaymentDTO> getScheduleByLoanId(Long loanId) {
        return paymentScheduleRepository.findByContractContractIdOrderByPaymentDateAsc(loanId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDTO> getPaymentsByLoanIdAndStatus(Long loanId, Boolean isPaid) {
        List<PaymentSchedule> payments = (isPaid == null)
                ? paymentScheduleRepository.findByContractContractIdOrderByPaymentDateAsc(loanId)
                : paymentScheduleRepository.findByContractContractIdAndIsPaidOrderByPaymentDateAsc(loanId, isPaid);

        return payments.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private PaymentDTO toDTO(PaymentSchedule payment) {
        return new PaymentDTO(
                payment.getPaymentId(),
                payment.getPaymentDate(),
                payment.getPrincipalAmount(),
                payment.getInterestAmount(),
                payment.getTotalPayment(),
                payment.getRateApplied(),
                payment.getIsPaid()
        );
    }
}


