package com.example.lending_service.dto;

public class ClientSearchDTO {
    private Long id;
    private String fullName;

    public ClientSearchDTO(Long id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }
}

