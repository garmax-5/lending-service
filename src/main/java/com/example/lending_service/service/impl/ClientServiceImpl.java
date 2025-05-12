package com.example.lending_service.service.impl;

import com.example.lending_service.dto.ClientDTO;
import com.example.lending_service.dto.ClientSearchDTO;
import com.example.lending_service.model.Client;
import com.example.lending_service.repository.ClientRepository;
import com.example.lending_service.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

//    @Override
//    public Client getClientById(Long id) {
//        return clientRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Client not found"));
//    }
//
//    @Override
//    public List<Client> getAllClients() {
//        return clientRepository.findAll();
//    }

    @Override
    public ClientDTO getClientById(Long id) {
        return toDTO(clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found")));
    }

    @Override
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClientSearchDTO> searchByFullName(String search) {
        return clientRepository.findByFullNameContainingIgnoreCase(search).stream()
                .map(c -> new ClientSearchDTO(c.getClientId(), c.getFullName()))
                .collect(Collectors.toList());
    }


    private ClientDTO toDTO(Client client) {
        return new ClientDTO(
                client.getClientId(),
                client.getFullName(),
                client.getPhone()
        );
    }
}

