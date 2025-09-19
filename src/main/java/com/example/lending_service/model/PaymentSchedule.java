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
@Table(name = "payment_schedule")
public class PaymentSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private LoanContract contract;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "principal_amount")
    private BigDecimal principalAmount;

    @Column(name = "interest_amount")
    private BigDecimal interestAmount;

    @Column(name = "total_payment")
    private BigDecimal totalPayment;

    @Column(name = "rate_applied")
    private BigDecimal rateApplied;

    @Column(name = "is_paid")
    private Boolean isPaid;
}
