package com.example.lending_service.service.impl;


import com.example.lending_service.dto.EmployeeDTO;
import com.example.lending_service.dto.EmployeeSearchDTO;
import com.example.lending_service.model.Employee;
import com.example.lending_service.repository.EmployeeRepository;
import com.example.lending_service.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

//    @Override
//    public Employee getEmployeeById(Long id) {
//        return employeeRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Employee not found"));
//    }
//
//    @Override
//    public List<Employee> getAllEmployees() {
//        return employeeRepository.findAll();
//    }
    @Override
    public EmployeeDTO getEmployeeById(Long id) {
        return toDTO(employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found")));
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeSearchDTO> searchByFullName(String search) {
        return employeeRepository.findByFullNameContainingIgnoreCase(search).stream()
                .map(e -> new EmployeeSearchDTO(e.getEmployeeId(), e.getFullName()))
                .collect(Collectors.toList());
    }


    private EmployeeDTO toDTO(Employee employee) {
        return new EmployeeDTO(
                employee.getEmployeeId(),
                employee.getFullName(),
                employee.getEmail(),
                employee.getRole().getRoleName()
        );
    }
}

