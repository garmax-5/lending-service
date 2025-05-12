package com.example.lending_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientDTO {
    private Long clientId;
    private String fullName;
    private String phone;
}

