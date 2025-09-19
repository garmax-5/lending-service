package com.example.lending_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanActionLogDTO {
    private Long id;
    private Long contractId;
    private String employeeName;
    private String actionType;
    private String description;
    private LocalDateTime actionTime;
}

