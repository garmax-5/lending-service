package com.example.lending_service.util;

import com.example.lending_service.model.LoanContract;
import com.example.lending_service.model.PaymentSchedule;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class Calculator {

    public List<PaymentSchedule> calculateAnnuitySchedule(
            LoanContract contract,
            BigDecimal remainingPrincipal,
            BigDecimal annualRate,
            int months,
            LocalDate firstPaymentDate
    ) {
        if (months <= 0 || remainingPrincipal.compareTo(BigDecimal.ZERO) <= 0) {
            return Collections.emptyList();
        }

        List<PaymentSchedule> schedule = new ArrayList<>();
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12 * 100), 10, BigDecimal.ROUND_HALF_UP);

        BigDecimal onePlusRPowerN = BigDecimal.ONE.add(monthlyRate).pow(months);
        BigDecimal annuityCoefficient = monthlyRate.multiply(onePlusRPowerN)
                .divide(onePlusRPowerN.subtract(BigDecimal.ONE), 10, BigDecimal.ROUND_HALF_UP);

        BigDecimal monthlyPayment = remainingPrincipal.multiply(annuityCoefficient)
                .setScale(2, BigDecimal.ROUND_HALF_UP);

        LocalDate paymentDate = firstPaymentDate;
        BigDecimal balance = remainingPrincipal;

        for (int i = 0; i < months; i++) {
            BigDecimal interestPayment = balance.multiply(monthlyRate).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal principalPayment = monthlyPayment.subtract(interestPayment).setScale(2, BigDecimal.ROUND_HALF_UP);

            // Корректировка последнего платежа
            if (i == months - 1) {
                principalPayment = balance;
                interestPayment = monthlyPayment.subtract(principalPayment).setScale(2, BigDecimal.ROUND_HALF_UP);
            }

            schedule.add(createPayment(contract, paymentDate, principalPayment, interestPayment, annualRate));

            balance = balance.subtract(principalPayment);
            paymentDate = paymentDate.plusMonths(1);
        }

        return schedule;
    }

    public List<PaymentSchedule> calculateDifferentiatedSchedule(
            LoanContract contract,
            BigDecimal remainingPrincipal,
            BigDecimal annualRate,
            int months,
            LocalDate firstPaymentDate
    ) {
        if (months <= 0 || remainingPrincipal.compareTo(BigDecimal.ZERO) <= 0) {
            return Collections.emptyList();
        }

        List<PaymentSchedule> schedule = new ArrayList<>();
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12 * 100), 10, BigDecimal.ROUND_HALF_UP);
        BigDecimal fixedPrincipal = remainingPrincipal.divide(BigDecimal.valueOf(months), 2, BigDecimal.ROUND_HALF_UP);

        LocalDate paymentDate = firstPaymentDate;
        BigDecimal balance = remainingPrincipal;

        for (int i = 0; i < months; i++) {
            BigDecimal principalPayment = (i == months - 1) ? balance : fixedPrincipal;
            BigDecimal interestPayment = balance.multiply(monthlyRate).setScale(2, BigDecimal.ROUND_HALF_UP);

            schedule.add(createPayment(contract, paymentDate, principalPayment, interestPayment, annualRate));

            balance = balance.subtract(principalPayment);
            paymentDate = paymentDate.plusMonths(1);
        }

        return schedule;
    }

    public static int calculateRemainingMonths(LocalDate from, LocalDate to) {
        return (int) java.time.temporal.ChronoUnit.MONTHS.between(from.withDayOfMonth(1), to.withDayOfMonth(1)) + 1;
    }

    private PaymentSchedule createPayment(
            LoanContract contract,
            LocalDate date,
            BigDecimal principal,
            BigDecimal interest,
            BigDecimal rate
    ) {
        return PaymentSchedule.builder()
                .contract(contract)
                .paymentDate(date)
                .principalAmount(principal)
                .interestAmount(interest)
                .totalPayment(principal.add(interest).setScale(2, BigDecimal.ROUND_HALF_UP))
                .rateApplied(rate)
                .isPaid(false)
                .build();
    }
}
