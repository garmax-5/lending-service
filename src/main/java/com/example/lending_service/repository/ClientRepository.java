package com.example.lending_service.repository;

import com.example.lending_service.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByFullNameContainingIgnoreCase(String fullName);
    Optional<Client> findByFullNameIgnoreCase(String fullName);

}