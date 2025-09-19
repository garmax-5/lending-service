package com.example.lending_service.repository;


import com.example.lending_service.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    List<Employee> findByFullNameContainingIgnoreCase(String fullName);
    Optional<Employee> findByEmailIgnoreCase(String email);
    List<Employee> findAllByOrderByFullNameAsc();
}


