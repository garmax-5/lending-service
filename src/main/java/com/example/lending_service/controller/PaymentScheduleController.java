package com.example.lending_service.controller;

import com.example.lending_service.dto.PaymentDTO;
import com.example.lending_service.model.PaymentSchedule;
import com.example.lending_service.service.PaymentScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class PaymentScheduleController {

    private final PaymentScheduleService paymentScheduleService;

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<PaymentDTO>> getScheduleByLoan(@PathVariable Long loanId) {
        return ResponseEntity.ok(paymentScheduleService.getScheduleByLoanId(loanId));
    }

    @GetMapping("/loan/{loanId}/filter")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByStatus(
            @PathVariable Long loanId,
            @RequestParam(required = false) Boolean isPaid
    ) {
        return ResponseEntity.ok(paymentScheduleService.getPaymentsByLoanIdAndStatus(loanId, isPaid));
    }
}

