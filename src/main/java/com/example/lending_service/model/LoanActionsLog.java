package com.example.lending_service.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "loan_actions_log")
public class LoanActionsLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private LoanContract contract;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private String actionType;
    private LocalDateTime actionTime;
    private String description;
}

