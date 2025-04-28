package com.example.lending_service.controller;

import com.example.lending_service.dto.LoanSummaryDTO;
import com.example.lending_service.dto.PaymentDTO;
import com.example.lending_service.model.LoanContract;
import com.example.lending_service.model.PaymentSchedule;
import com.example.lending_service.service.LoanContractService;
import com.example.lending_service.service.PaymentScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanContractController {

    private final LoanContractService loanContractService;
    private final PaymentScheduleService paymentScheduleService;

    @PostMapping
    public ResponseEntity<LoanContract> createLoan(@RequestBody LoanContract loanContract) {
        return ResponseEntity.ok(loanContractService.createLoan(loanContract));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanContract> getLoan(@PathVariable Long id) {
        return ResponseEntity.ok(loanContractService.getLoan(id));
    }

    @GetMapping
    public ResponseEntity<List<LoanContract>> getAllLoans() {
        return ResponseEntity.ok(loanContractService.getAllLoans());
    }

    @PostMapping("/{id}/calculate-schedule")
    public ResponseEntity<String> calculateSchedule(@PathVariable Long id) {
        loanContractService.calculateSchedule(id);
        return ResponseEntity.ok("Schedule calculated successfully.");
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<LoanSummaryDTO> getLoanSummary(@PathVariable Long id) {
        return ResponseEntity.ok(loanContractService.getLoanSummary(id));
    }

    @GetMapping("/{id}/payments")
    public ResponseEntity<List<PaymentDTO>> getPayments(
            @PathVariable Long id,
            @RequestParam(required = false) Boolean isPaid
    ) {
        return ResponseEntity.ok(paymentScheduleService.getPaymentsByLoanIdAndStatus(id, isPaid));
    }
}
