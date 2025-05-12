package com.example.lending_service.controller;


import com.example.lending_service.dto.ClientSearchDTO;
import com.example.lending_service.model.Client;
import com.example.lending_service.dto.ClientDTO;
import com.example.lending_service.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

//    @GetMapping("/{id}")
//    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
//        return ResponseEntity.ok(clientService.getClientById(id));
//    }
//
//    @GetMapping
//    public ResponseEntity<List<Client>> getAllClients() {
//        return ResponseEntity.ok(clientService.getAllClients());
//    }
    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @GetMapping
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ClientSearchDTO>> search(@RequestParam String search) {
        List<ClientSearchDTO> results = clientService.searchByFullName(search);
        return ResponseEntity.ok(results);
    }
}

