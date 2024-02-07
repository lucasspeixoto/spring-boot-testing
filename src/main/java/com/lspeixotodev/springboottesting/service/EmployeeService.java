package com.lspeixotodev.springboottesting.service;

import com.lspeixotodev.springboottesting.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    Employee saveEmployee(Employee employee) throws Exception;

    List<Employee> getAllEmployees();

    Optional<Employee> getEmployeeById(Long id) throws Exception;

    Employee updateEmployee(Employee employee) throws Exception;

    Employee deleteEmployee(Employee employee) throws Exception;
}
