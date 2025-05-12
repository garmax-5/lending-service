package com.example.lending_service.service;

import com.example.lending_service.dto.PaymentDTO;
import com.example.lending_service.model.PaymentSchedule;

import java.util.List;

public interface PaymentScheduleService {
//    List<PaymentSchedule> getSchedulesByLoanId(Long loanId);

    List<PaymentDTO> getScheduleByLoanId(Long loanId);

    List<PaymentDTO> getPaymentsByLoanIdAndStatus(Long loanId, Boolean isPaid);
}
