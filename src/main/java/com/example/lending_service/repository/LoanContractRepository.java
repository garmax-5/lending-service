package com.example.lending_service.repository;

import com.example.lending_service.model.LoanContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanContractRepository extends JpaRepository<LoanContract, Long> {
}

