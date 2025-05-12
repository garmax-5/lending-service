package com.example.lending_service.service.impl;

import com.example.lending_service.dto.PaymentDTO;
import com.example.lending_service.model.PaymentSchedule;
import com.example.lending_service.repository.PaymentScheduleRepository;
import com.example.lending_service.service.PaymentScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class PaymentScheduleServiceImpl implements PaymentScheduleService {

    private final PaymentScheduleRepository paymentScheduleRepository;

//    @Override
//    public List<PaymentSchedule> getSchedulesByLoanId(Long loanId) {
//        return paymentScheduleRepository.findByContractContractId(loanId);
//    }

    @Override
    public List<PaymentDTO> getScheduleByLoanId(Long loanId) {
        List<PaymentSchedule> payments = paymentScheduleRepository.findByContractContractId(loanId);

        return payments.stream()
                .sorted(Comparator.comparing(PaymentSchedule::getPaymentDate)) // ← сортировка по дате
                .map(p -> new PaymentDTO(
                        p.getPaymentId(),
                        p.getPaymentDate(),
                        p.getPrincipalAmount(),
                        p.getInterestAmount(),
                        p.getTotalPayment(),
                        p.getRateApplied(),
                        p.isPaid()
                ))
                .collect(Collectors.toList());
    }


    @Override
    public List<PaymentDTO> getPaymentsByLoanIdAndStatus(Long loanId, Boolean isPaid) {
        List<PaymentSchedule> payments;

        if (isPaid == null) {
            payments = paymentScheduleRepository.findByContractContractId(loanId);
        } else {
            payments = paymentScheduleRepository.findByContractContractIdAndIsPaid(loanId, isPaid);
        }

        return payments.stream()
                .map(payment -> new PaymentDTO(
                        payment.getPaymentId(),
                        payment.getPaymentDate(),
                        payment.getPrincipalAmount(),
                        payment.getInterestAmount(),
                        payment.getTotalPayment(),
                        payment.getRateApplied(),
                        payment.isPaid()
                ))
                .collect(Collectors.toList());
    }
}
