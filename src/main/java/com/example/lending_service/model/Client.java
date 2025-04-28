package com.example.lending_service.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientId;

    private String fullName;
    private LocalDate birthDate;
    private String passportSeries;
    private String passportNumber;
    private String phone;
    private String email;
}
