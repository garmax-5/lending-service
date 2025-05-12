package com.example.lending_service.repository;

import com.example.lending_service.model.InterestRateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterestRateHistoryRepository extends JpaRepository<InterestRateHistory, Long> {
    List<InterestRateHistory> findByContractContractId(Long contractId);
}
