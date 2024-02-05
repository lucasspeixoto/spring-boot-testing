package com.lspeixotodev.springboottesting.controller;

import static org.mockito.BDDMockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lspeixotodev.springboottesting.exception.ResourceAlreadyExistsException;
import com.lspeixotodev.springboottesting.model.Employee;
import com.lspeixotodev.springboottesting.service.impl.EmployeeServiceImpl;
import com.lspeixotodev.springboottesting.utils.constants.MediaType;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebMvcTest(EmployeeController.class)
@DisplayName("Employee Controller Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /*
    O @MockBean informa ao Spring que é necessário criar um mock
    da instância de EmployeeService e adiciona isso ao contexto
    da aplicação e pode ser injetado no EmployeeController
     */
    @MockBean
    private EmployeeServiceImpl employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    @DisplayName("JUnit test for create Employee method (Success Case)")
    public void givenEmployeeObject_WhenCreateEmployee_ThenReturnSavedEmployee() throws Exception {
        //given - precondition or setup
        Employee employee = Employee.builder()
                .firstName("Lucas")
                .lastName("Peixoto")
                .email("lspeixotodev@gmail.com")
                .age(32)
                .build();

        given(employeeService.saveEmployee(
                ArgumentMatchers.any(Employee.class))
        ).willAnswer((invocation) -> invocation.getArgument(0));

        //when - action or the behaviour we`re testing
        ResultActions response = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee))

        );

        //then - verify the result or output using assert statements
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName",
                        CoreMatchers.is(employee.getFirstName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName",
                        CoreMatchers.is(employee.getLastName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email",
                        CoreMatchers.is(employee.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age",
                        CoreMatchers.is(employee.getAge())));
    }

    @Test
    @Order(2)
    @DisplayName("JUnit test for create Employee method (Failed Case)")
    void givenEmployeeObject_WhenCreateEmployee_thenThrowAnException() throws Exception {
        //given - precondition or setup
        Employee employee = Employee.builder()
                .id(1L)
                .firstName("Lucas")
                .lastName("Peixoto")
                .email("lspeixotodev@gmail.com")
                .age(31)
                .build();

        String alreadyExistsErrorMessage = "Employee Already exists with given email: " + employee.getEmail();

        given(employeeService.saveEmployee(any(Employee.class)))
                .willThrow(new ResourceAlreadyExistsException(alreadyExistsErrorMessage));

        //when - action or the behaviour we`re testing
        ResultActions response = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/employees", employee)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee))
        );

        //then - verify the result or output using assert statements
        response
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is(alreadyExistsErrorMessage)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @Order(3)
    @DisplayName("JUnit test for get All Employees method")
    void givenListOfEmployees_whenGetAllEmployees_thenReturnEmployeesList() throws Exception {

        //given - precondition or setup
        List<Employee> listOfEmployees = new ArrayList<>();

        Employee employee1 = Employee.builder()
                .firstName("Lucas")
                .lastName("Peixoto")
                .email("lspeixotodev@gmail.com")
                .age(32)
                .build();
        Employee employee2 = Employee.builder()
                .firstName("Liana")
                .lastName("Fernandes")
                .email("lianacgf@gmail.com")
                .age(29)
                .build();

        listOfEmployees.add(employee1);
        listOfEmployees.add(employee2);

        given(employeeService.getAllEmployees()).willReturn(listOfEmployees);

        //when - action or the behaviour we`re testing
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/employees"));

        //then - verify the result or output using assert statements
        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(listOfEmployees.size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName",
                        CoreMatchers.is(employee1.getFirstName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].lastName",
                        CoreMatchers.is(employee1.getLastName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email",
                        CoreMatchers.is(employee1.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age",
                        CoreMatchers.is(employee1.getAge())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].firstName",
                        CoreMatchers.is(employee2.getFirstName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].lastName",
                        CoreMatchers.is(employee2.getLastName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].email",
                        CoreMatchers.is(employee2.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].age",
                        CoreMatchers.is(employee2.getAge())));
    }

    @Test
    @Order(4)
    @DisplayName("JUnit test for get Employee by Id method (Success Case)")
    void givenEmployeeObject_whenGetEmployeeById_thenReturnEmployee() throws Exception {

        //given - precondition or setup
        Long employeeId = 1L;

        Employee employee = Employee.builder()
                .firstName("Lucas")
                .lastName("Peixoto")
                .email("lspeixotodev@gmail.com")
                .age(32)
                .build();


        given(employeeService.getEmployeeById(employeeId)).willReturn(Optional.of(employee));

        //when - action or the behaviour we`re testing
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/{id}", employeeId));

        //then - verify the result or output using assert statements
        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName",
                        CoreMatchers.is(employee.getFirstName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName",
                        CoreMatchers.is(employee.getLastName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email",
                        CoreMatchers.is(employee.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age",
                        CoreMatchers.is(employee.getAge())));
    }

    @Test
    @Order(5)
    @DisplayName("JUnit test for get Employee by Id method (Failed Case)")
    void givenEmployeeObject_whenGetEmployeeById_thenThrowAnException() throws Exception {
        //given - precondition or setup
        Long employeeId = 1L;

        String doesNotExistsErrorMessage = "Employee Does not exists with given Id: " + employeeId;

        given(employeeService.getEmployeeById(anyLong()))
                .willThrow(new ResourceAlreadyExistsException(doesNotExistsErrorMessage));


        //when - action or the behaviour we`re testing
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/{id}", employeeId));

        //then - verify the result or output using assert statements
        response
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is(doesNotExistsErrorMessage)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(6)
    @DisplayName("JUnit test for update method (Success Case)")
    void givenEmployeeObject_whenUpdate_thenReturnUpdatedEmployee() throws Exception {
        //given - precondition or setup
        Employee employee = Employee.builder()
                .id(1L)
                .firstName("Lucas")
                .lastName("Peixoto")
                .email("lspeixotodev@gmail.com")
                .age(31)
                .build();

        Employee updatedEmployee = Employee.builder()
                .id(1L)
                .firstName("Lucas")
                .lastName("Peixoto")
                .email("lspeixotodev@gmail.com")
                .age(32)
                .build();

        given(employeeService.getEmployeeById(employee.getId())).willReturn(Optional.of(updatedEmployee));

        given(employeeService.updateEmployee(any(Employee.class))).willAnswer(
                (invocation -> invocation.getArgument(0))
        );

        //when - action or the behaviour we`re testing
        ResultActions response = mockMvc.perform(
                MockMvcRequestBuilders.put("/api/employees", employee)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEmployee))
        );

        //then - verify the result or output using assert statements
        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age",
                        CoreMatchers.is(updatedEmployee.getAge())));
    }

    @Test
    @Order(7)
    @DisplayName("JUnit test for update method (Failed Case)")
    void givenEmployeeObject_whenUpdate_thenThrowAnException() throws Exception {

        //given - precondition or setup
        long employeeId = 1L;

        Employee employee = Employee.builder()
                .id(1L)
                .firstName("Lucas")
                .lastName("Peixoto")
                .email("lspeixotodev@gmail.com")
                .age(31)
                .build();

        String doesNotExistsErrorMessage = "Employee Does not exists with given Id: " + employeeId;

        given(employeeService.updateEmployee(any(Employee.class)))
                .willThrow(new ResourceAlreadyExistsException(doesNotExistsErrorMessage));

        //when - action or the behaviour we`re testing
        ResultActions response = mockMvc.perform(
                MockMvcRequestBuilders.put("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee))
        );

        //then - verify the result or output using assert statements
        response
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is(doesNotExistsErrorMessage)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }
}