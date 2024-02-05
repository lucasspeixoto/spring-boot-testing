package com.lspeixotodev.springboottesting.service.impl;

import com.lspeixotodev.springboottesting.exception.ResourceAlreadyExistsException;
import com.lspeixotodev.springboottesting.exception.ResourceNotFoundException;
import com.lspeixotodev.springboottesting.model.Employee;
import com.lspeixotodev.springboottesting.repository.EmployeeRepository;
import com.lspeixotodev.springboottesting.service.EmployeeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeServiceImpl(EmployeeRepository repository) {

        this.repository = repository;
    }

    @Override
    public Employee saveEmployee(Employee employee) throws Exception {

        String email = employee.getEmail();

        Optional<Employee> optionalEmployee = repository.findByEmail(email);

        if (optionalEmployee.isPresent()) {
            throw new ResourceAlreadyExistsException("Employee Already exists with given email: " + email);
        }

        return repository.save(employee);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return repository.findAll();
    }

    @Override
    public Optional<Employee> getEmployeeById(Long id) throws Exception {

        Optional<Employee> optionalExistingEmployee = repository.findById(id);

        if (optionalExistingEmployee.isEmpty()) {
            throw new ResourceNotFoundException("Employee Does not exists with given id: " + id);
        }

        return optionalExistingEmployee;

    }

    @Override
    public Employee updateEmployee(Employee employee) throws Exception {

        Long employeeId = employee.getId();

        Employee updatedEmployee = new Employee();

        try {
            Optional<Employee> optionalExistingEmployee = getEmployeeById(employeeId);

            if(optionalExistingEmployee.isPresent()) {
                updatedEmployee = optionalExistingEmployee.get();

                updatedEmployee.setFirstName(employee.getFirstName());
                updatedEmployee.setLastName(employee.getLastName());
                updatedEmployee.setEmail(employee.getEmail());
                updatedEmployee.setAge(employee.getAge());
            }
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Employee Does not exists with given Id: " + employeeId);
        }


        return repository.save(updatedEmployee);

    }

    @Override
    public void deleteEmployee(Long id) {

        Optional<Employee> optionalExistingEmployee = repository.findById(id);

        if (optionalExistingEmployee.isEmpty()) {
            throw new ResourceNotFoundException("Employee Does not exists with given id: " + id);
        }

        repository.deleteById(id);
    }

    public Employee buildNewEmployee(Employee employee) {

        return Employee.builder()
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .age(employee.getAge())
                .build();
    }
}
