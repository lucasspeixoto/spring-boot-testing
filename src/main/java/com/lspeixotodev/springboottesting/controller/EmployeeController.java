package com.lspeixotodev.springboottesting.controller;

import com.lspeixotodev.springboottesting.model.Employee;
import com.lspeixotodev.springboottesting.service.impl.EmployeeServiceImpl;
import com.lspeixotodev.springboottesting.utils.constants.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/employees", produces = MediaType.APPLICATION_JSON)
public class EmployeeController {

    @Autowired
    private EmployeeServiceImpl employeeService;

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON},
            produces = {MediaType.APPLICATION_JSON}
    )
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) throws Exception {
        Employee createdEmployee = employeeService.saveEmployee(employee);

        return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);

    }

    @GetMapping()
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();

        return ResponseEntity.status(HttpStatus.OK).body(employees);
    }

    @GetMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON}
    )
    public ResponseEntity<Optional<Employee>> getEmployeeById(@PathVariable Long id) throws Exception {

        Optional<Employee> employee = employeeService.getEmployeeById(id);

        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @PutMapping(
            consumes = {MediaType.APPLICATION_JSON},
            produces = {MediaType.APPLICATION_JSON}
    )
    public ResponseEntity<Employee> updateEmployee(@RequestBody Employee employee) throws Exception {

        Employee updatedEmployee = employeeService.updateEmployee(employee);

        return new ResponseEntity<>(updatedEmployee, HttpStatus.OK);

    }

    @DeleteMapping(
            consumes = {MediaType.APPLICATION_JSON},
            produces = {MediaType.APPLICATION_JSON}
    )
    public ResponseEntity<Employee> deleteEmployee(@RequestBody Employee employee) throws Exception {

        Employee deletedEmployee = employeeService.deleteEmployee(employee);

        return new ResponseEntity<>(deletedEmployee, HttpStatus.OK);
    }

}
