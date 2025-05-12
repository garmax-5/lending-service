package com.example.lending_service.service;


import com.example.lending_service.dto.ClientDTO;
import com.example.lending_service.dto.ClientSearchDTO;
import com.example.lending_service.model.Client;

import java.util.List;

public interface ClientService {
//    Client getClientById(Long id);
//    List<Client> getAllClients();

    ClientDTO getClientById(Long id);
    List<ClientDTO> getAllClients();
    List<ClientSearchDTO> searchByFullName(String search);

}

