package com.example.lending_service.model;

import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "interest_rate_history")
public class InterestRateHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rateId;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private LoanContract contract;

    private BigDecimal rate;
    private LocalDate startDate;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
