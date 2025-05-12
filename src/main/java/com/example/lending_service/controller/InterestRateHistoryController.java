package com.example.lending_service.controller;

import com.example.lending_service.model.InterestRateHistory;
import com.example.lending_service.service.InterestRateHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rates")
@RequiredArgsConstructor
class InterestRateHistoryController {

    private final InterestRateHistoryService interestRateHistoryService;

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<InterestRateHistory>> getRateHistoryByLoan(@PathVariable Long loanId) {
        return ResponseEntity.ok(interestRateHistoryService.getRateHistoryByLoanId(loanId));
    }

    @PutMapping("/loan/{loanId}")
    public ResponseEntity<String> updateRate(
            @PathVariable Long loanId,
            @RequestParam BigDecimal newRate,
            @RequestParam LocalDate startDate,
            @RequestParam Long employeeId
    ) {
        interestRateHistoryService.updateInterestRate(loanId, newRate, startDate, employeeId);
        return ResponseEntity.ok("The interest rate has been successfully updated and the schedule has been recalculated.");
    }
}
