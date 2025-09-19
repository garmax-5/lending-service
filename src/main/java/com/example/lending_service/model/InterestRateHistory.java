package com.example.lending_service.model;

import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "interest_rate_history")
public class InterestRateHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rate_id")
    private Long rateId;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private LoanContract contract;

    @Column(name = "rate")
    private BigDecimal rate;

    @Column(name = "start_date")
    private LocalDate startDate;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
