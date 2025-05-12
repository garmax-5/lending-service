package com.example.lending_service.repository;

import com.example.lending_service.model.PaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentScheduleRepository extends JpaRepository<PaymentSchedule, Long> {
    // Найти все платежи по контракту
    List<PaymentSchedule> findByContractContractId(Long contractId);

    // Найти все платежи по контракту с фильтрацией по оплате
    List<PaymentSchedule> findByContractContractIdAndIsPaid(Long contractId, boolean isPaid);
}
