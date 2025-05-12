package com.example.lending_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeDTO {
    private Long employeeId;
    private String fullName;
    private String email;
    private String roleName;
}

