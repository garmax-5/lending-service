package com.example.lending_service.service.impl;

import com.example.lending_service.dto.ClientDTO;
import com.example.lending_service.dto.ClientSearchDTO;
import com.example.lending_service.model.Client;
import com.example.lending_service.repository.ClientRepository;
import com.example.lending_service.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    public ClientDTO getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        return toDTO(client);
    }

    @Override
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAllByOrderByFullNameAsc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClientSearchDTO> getAllClientsForSearch() {
        return clientRepository.findAllByOrderByFullNameAsc()
                .stream()
                .map(this::toSearchDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClientSearchDTO> searchByFullName(String search) {
        return clientRepository.findByFullNameContainingIgnoreCase(search)
                .stream()
                .map(this::toSearchDTO)
                .collect(Collectors.toList());
    }

    private ClientDTO toDTO(Client client) {
        return new ClientDTO(
                client.getClientId(),
                client.getFullName(),
                client.getPhone()
        );
    }

    private ClientSearchDTO toSearchDTO(Client client) {
        return new ClientSearchDTO(client.getClientId(), client.getFullName());
    }
}




