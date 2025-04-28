package com.example.lending_service.service;


import com.example.lending_service.model.Client;

import java.util.List;

public interface ClientService {
    Client getClientById(Long id);
    List<Client> getAllClients();
}

