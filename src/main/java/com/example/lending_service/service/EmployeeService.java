package com.example.lending_service.service;


import com.example.lending_service.dto.EmployeeDTO;
import com.example.lending_service.dto.EmployeeSearchDTO;
import com.example.lending_service.model.Employee;

import java.util.List;

public interface EmployeeService {
//    Employee getEmployeeById(Long id);
//    List<Employee> getAllEmployees();

    EmployeeDTO getEmployeeById(Long id);
    List<EmployeeDTO> getAllEmployees();
    List<EmployeeSearchDTO> searchByFullName(String search);

}

