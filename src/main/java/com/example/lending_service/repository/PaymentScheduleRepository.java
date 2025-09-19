package com.example.lending_service.repository;

import com.example.lending_service.model.PaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentScheduleRepository extends JpaRepository<PaymentSchedule, Long> {
    List<PaymentSchedule> findByContractContractId(Long contractId);
    List<PaymentSchedule> findByContractContractIdOrderByPaymentDateAsc(Long contractId);
    List<PaymentSchedule> findByContractContractIdAndIsPaidOrderByPaymentDateAsc(Long contractId, Boolean isPaid);
}


