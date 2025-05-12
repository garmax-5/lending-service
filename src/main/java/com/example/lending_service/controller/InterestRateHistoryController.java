package com.example.lending_service.controller;

import com.example.lending_service.dto.UpdateRateRequest;
import com.example.lending_service.model.Employee;
import com.example.lending_service.model.InterestRateHistory;
import com.example.lending_service.repository.EmployeeRepository;
import com.example.lending_service.service.InterestRateHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/rates")
@RequiredArgsConstructor
public class InterestRateHistoryController {

    private final InterestRateHistoryService interestRateHistoryService;
    private final EmployeeRepository employeeRepository;

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<InterestRateHistory>> getRateHistoryByLoan(@PathVariable Long loanId) {
        return ResponseEntity.ok(interestRateHistoryService.getRateHistoryByLoanId(loanId));
    }

    @PutMapping("/loan/{loanId}")
    public ResponseEntity<String> updateInterestRate(
            @PathVariable Long loanId,
            @RequestBody @Valid UpdateRateRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Сотрудник не найден"));

        interestRateHistoryService.updateInterestRate(
                loanId,
                request.getNewRate(),
                request.getStartDate(),
                employee.getEmployeeId()
        );

        return ResponseEntity.ok("Ставка успешно обновлена.");
    }
}
