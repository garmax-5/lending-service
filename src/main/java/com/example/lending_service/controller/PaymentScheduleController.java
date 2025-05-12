package com.example.lending_service.controller;

import com.example.lending_service.dto.PaymentDTO;
import com.example.lending_service.model.PaymentSchedule;
import com.example.lending_service.service.PaymentScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
class PaymentScheduleController {

    private final PaymentScheduleService paymentScheduleService;

//    @GetMapping("/loan/{loanId}")
//    public ResponseEntity<List<PaymentSchedule>> getSchedulesByLoan(@PathVariable Long loanId) {
//        return ResponseEntity.ok(paymentScheduleService.getSchedulesByLoanId(loanId));
//    }

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<PaymentDTO>> getScheduleByLoan(@PathVariable Long loanId) {
        return ResponseEntity.ok(paymentScheduleService.getScheduleByLoanId(loanId));
    }
}
