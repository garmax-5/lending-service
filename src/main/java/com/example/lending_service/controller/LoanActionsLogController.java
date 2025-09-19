package com.example.lending_service.controller;

import com.example.lending_service.dto.LoanActionLogDTO;
import com.example.lending_service.model.LoanActionsLog;
import com.example.lending_service.service.LoanActionsLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LoanActionsLogController {

    private final LoanActionsLogService loanActionsLogService;

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<LoanActionLogDTO>> getLogsByLoan(@PathVariable Long loanId) {
        return ResponseEntity.ok(loanActionsLogService.getLogsByLoanId(loanId));
    }
}

