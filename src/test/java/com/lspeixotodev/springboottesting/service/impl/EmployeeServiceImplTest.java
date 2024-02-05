package com.lspeixotodev.springboottesting.service.impl;

import com.lspeixotodev.springboottesting.exception.GlobalExceptionHandler;
import com.lspeixotodev.springboottesting.exception.ResourceAlreadyExistsException;
import com.lspeixotodev.springboottesting.exception.ResourceNotFoundException;
import com.lspeixotodev.springboottesting.model.Employee;

import static org.assertj.core.api.Assertions.*;

import com.lspeixotodev.springboottesting.repository.EmployeeRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.BDDMockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@DisplayName("Employee Service Test")
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;

    @BeforeEach
    public void setUp() {
        /*
         * employeeRepository = Mockito.mock(EmployeeRepository.class);
         * employeeService = new EmployeeServiceImpl(employeeRepository);
         */

        employee = Employee.builder()
                .id(1L)
                .firstName("Lucas")
                .lastName("Peixoto")
                .email("lspeixotodev@gmail.com")
                .age(32).build();
    }


    @DisplayName("JUnit test for saveEmployee method (Success case)")
    @Order(1)
    @Test
    public void givenEmployeeObject_WhenSaveEmployee_ThenReturnEmployeeObject() throws Exception {
        //given - precondition or setup
        given(employeeRepository.findByEmail(employee.getEmail()))
                .willReturn(Optional.empty());

        given(employeeRepository.save(employee))
                .willReturn(employee);

        //when - action or the behaviour we`re testing
        Employee savedEmployee = employeeService.saveEmployee(employee);

        //then - verify the output
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getFirstName()).isEqualTo("Lucas");
        assertThat(savedEmployee.getAge()).isEqualTo(32);
        assertThat(savedEmployee.getLastName()).isEqualTo("Peixoto");
    }

    @DisplayName("JUnit test for saveEmployee method (Failed case)")
    @Order(2)
    @Test
    public void givenExistingEmail_WhenSaveEmployee_ThenThrowsException() {
        //given - precondition or setup
        given(employeeRepository.findByEmail(employee.getEmail()))
                .willReturn(Optional.of(employee));

        //when - action or the behaviour we`re testing
        org.junit.jupiter.api.Assertions.assertThrows(
                ResourceAlreadyExistsException.class,
                () -> employeeService.saveEmployee(employee)
        );

        //then - verify the output
        verify(employeeRepository, never())
                .save(any(Employee.class));

    }

    @DisplayName("JUnit test for getAllEmployee method")
    @Order(3)
    @Test
    public void givenEmployeesList_WhenGetAllEmployees_ThenReturnEmployeesList() {
        //given - precondition or setup
        Employee newEmployee = Employee.builder()
                .id(2L)
                .firstName("Liana")
                .lastName("Fernandes")
                .email("lianacgf@gmail.com")
                .age(29).build();

        given(employeeRepository.findAll())
                .willReturn(List.of(employee, newEmployee));

        //when - action or the behaviour we`re testing
        List<Employee> employees = employeeService.getAllEmployees();

        //then - verify the output
        assertThat(employees).isNotNull();
        assertThat(employees.size()).isEqualTo(2);

    }

    @DisplayName("JUnit test for getAllEmployee method with empty list")
    @Order(4)
    @Test
    public void givenEmptyEmployeesList_WhenGetAllEmployees_ThenReturnEmptyEmployeesList() {
        //given - precondition or setup
        given(employeeRepository.findAll())
                .willReturn(Collections.emptyList());

        //when - action or the behaviour we`re testing
        List<Employee> employees = employeeService.getAllEmployees();

        //then - verify the output
        assertThat(employees).isEmpty();

    }

    @DisplayName("JUnit test for find employee by id method Success case")
    @Order(5)
    @Test
    public void givenEmployeeObject_WhenFindById_ThenReturnEmployeeObject() throws Exception {
        //given - precondition or setup
        given(employeeRepository.findById(1L))
                .willReturn(Optional.of(employee));

        //when - action or the behaviour we`re testing
        Employee savedEmployee = employeeService.getEmployeeById(employee.getId()).get();

        //then - verify the output
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getFirstName()).isEqualTo("Lucas");
        assertThat(savedEmployee.getAge()).isEqualTo(32);
        assertThat(savedEmployee.getLastName()).isEqualTo("Peixoto");
    }

    @DisplayName("JUnit test for update employee method Success case")
    @Order(5)
    @Test
    public void givenEmployeeObject_WhenUpdate_ThenReturnUpdatedEmployeeObject() throws Exception {
        //given - precondition or setup
        given(employeeRepository.findById(1L))
                .willReturn(Optional.of(employee));

        employee.setFirstName("Arnold");
        employee.setLastName("Schwarzenegger");

        given(employeeRepository.save(employee))
                .willReturn(employee);

        //when - action or the behaviour we`re testing
        employeeService.updateEmployee(employee);

        Employee updatedEmployee = employeeService.getEmployeeById(employee.getId()).get();

        //then - verify the output
        assertThat(updatedEmployee).isNotNull();
        assertThat(updatedEmployee.getFirstName()).isEqualTo("Arnold");
        assertThat(updatedEmployee.getAge()).isEqualTo(32);
        assertThat(updatedEmployee.getLastName()).isEqualTo("Schwarzenegger");
    }

    @DisplayName("JUnit test for update employee method Failed case")
    @Order(6)
    @Test
    public void givenEmployeeObject_WhenUpdate_ThenThrowsAnException() {
        //given - precondition or setup
        given(employeeRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //when - action or the behaviour we`re testing
        org.junit.jupiter.api.Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.updateEmployee(employee)
        );

        //then - verify the output
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @DisplayName("JUnit test for delete employee method Success case")
    @Order(7)
    @Test
    public void givenEmployeeId_WhenDeleteById_ThenNothing() {
        //given - precondition or setup
        willDoNothing().given(employeeRepository).deleteById(employee.getId());

        given(employeeRepository.findById(employee.getId()))
                .willReturn(Optional.of(employee));

        //when - action or the behaviour we`re testing
        employeeService.deleteEmployee(employee.getId());

        //then - verify the output
        verify(employeeRepository, times(1))
                .deleteById(employee.getId());

    }

    @DisplayName("JUnit test for delete employee method Failed case")
    @Order(8)
    @Test
    public void givenEmployeeId_WhenDeleteById_ThenThrowAnException() {
        //given - precondition or setup

        given(employeeRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //when - action or the behaviour we`re testing
        org.junit.jupiter.api.Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.deleteEmployee(2L)
        );

        //then - verify the output
        verify(employeeRepository, never()).deleteById(anyLong());

    }

}