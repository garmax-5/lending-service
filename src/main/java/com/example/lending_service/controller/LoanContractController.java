package com.example.lending_service.controller;

import com.example.lending_service.dto.CreateLoanRequest;
import com.example.lending_service.dto.LoanContractDTO;
import com.example.lending_service.dto.LoanSummaryDTO;
import com.example.lending_service.dto.PaymentDTO;
import com.example.lending_service.model.LoanContract;
import com.example.lending_service.model.PaymentSchedule;
import com.example.lending_service.service.LoanContractService;
import com.example.lending_service.service.PaymentScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanContractController {

    private final LoanContractService loanContractService;
    private final PaymentScheduleService paymentScheduleService;


    @PostMapping
    public ResponseEntity<LoanContractDTO> createLoan(
            @RequestBody @Valid CreateLoanRequest request,
            Authentication authentication
    ) {
        String employeeEmail = authentication.getName(); // получаем email из токена
        LoanContract contract = loanContractService.createLoan(request, employeeEmail);
        return ResponseEntity.ok(toDTO(contract));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanContractDTO> getLoan(@PathVariable Long id) {
        return ResponseEntity.ok(loanContractService.getLoan(id));
    }

    @GetMapping
    public ResponseEntity<List<LoanContractDTO>> getAllLoans() {
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
