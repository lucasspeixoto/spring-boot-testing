package com.lspeixotodev.springboottesting.integration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lspeixotodev.springboottesting.SpringBootTestingApplication;
import com.lspeixotodev.springboottesting.model.Employee;
import com.lspeixotodev.springboottesting.repository.EmployeeRepository;
import com.lspeixotodev.springboottesting.testcontainers.AbstractIntegrationTest;
import com.lspeixotodev.springboottesting.utils.constants.MediaType;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = SpringBootTestingApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Employee Controller (Integration Testing With TestContainers)")
public class EmployeeControllerITCTests extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private static ObjectMapper objectMapper;

    @BeforeAll
    public static void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @BeforeEach
    public void config() {
        employeeRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Integration Test to Create a Employee (Success Case)")
    public void givenEmployeeObject_WhenCreateEmployee_ThenReturnSavedEmployee() throws Exception {
        //given - precondition or setup
        Employee employee = Employee.builder()
                .firstName("Lucas")
                .lastName("Peixoto")
                .email("lspeixotodev@gmail.com")
                .age(32)
                .build();

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
    @DisplayName("Integration Test to Create a Employee (Failed Case)")
    void givenEmployeeObject_WhenCreateEmployee_thenReturn404() throws Exception {
        //given - precondition or setup
        Employee employee1 = Employee.builder()
                .firstName("Lucas")
                .lastName("Peixoto")
                .email("lspeixotodev@gmail.com")
                .age(31)
                .build();

        Employee employee2 = Employee.builder()
                .firstName("Lucas")
                .lastName("Peixoto Fernandes")
                .email("lspeixotodev@gmail.com")
                .age(32)
                .build();

        String alreadyExistsErrorMessage = "Employee Already exists with given email: " + employee2.getEmail();

        //when - action or the behaviour we`re testing
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/employees", employee1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee1))
        );

        ResultActions response = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/employees", employee2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee2))
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
    @DisplayName("Integration test for get All Employees method")
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
                .email("lianacgf@hotmail.com")
                .age(30)
                .build();

        listOfEmployees.add(employee1);
        listOfEmployees.add(employee2);

        employeeRepository.saveAll(listOfEmployees);

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
    @DisplayName("Integration test for get Employee by Id method (Success Case)")
    void givenEmployeeObject_whenGetEmployeeById_thenReturnEmployee() throws Exception {

        //given - precondition or setup
        Employee employee = Employee.builder()
                .firstName("Lucas")
                .lastName("Peixoto")
                .email("lspeixotodev@gmail.com")
                .age(32)
                .build();

        Employee savedEmployee = employeeRepository.save(employee);

        //when - action or the behaviour we`re testing
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/{id}", savedEmployee.getId()));

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
    @DisplayName("Integration test for get Employee by Id method (Failed Case)")
    void givenEmployeeObject_whenGetEmployeeById_thenReturn404() throws Exception {
        //given - precondition or setup
        long employeeId = 1L;
        String doesNotExistsErrorMessage = "Employee Does not exists with given id: " + employeeId;

        //when - action or the behaviour we`re testing
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/{id}", employeeId));

        //then - verify the result or output using assert statements
        response
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        CoreMatchers.is(doesNotExistsErrorMessage))
                )
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(6)
    @DisplayName("Integration test for Update Employee (Success Case)")
    void givenEmployeeObject_whenUpdate_thenReturnUpdatedEmployee() throws Exception {
        //given - precondition or setup
        Employee employee = Employee.builder()
                .firstName("Lucas")
                .lastName("Peixoto")
                .email("lspeixotodev@gmail.com")
                .age(31)
                .build();

        Employee savedEmployee = employeeRepository.save(employee);

        Employee updatedEmployee = Employee.builder()
                .id(savedEmployee.getId())
                .firstName("Lucas")
                .lastName("Peixoto Fernandes")
                .email("lspeixotodev@gmail.com")
                .age(32)
                .build();

        //when - action or the behaviour we`re testing
        ResultActions response = mockMvc.perform(
                MockMvcRequestBuilders.put("/api/employees", updatedEmployee)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEmployee))
        );

        //then - verify the result or output using assert statements
        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age",
                        CoreMatchers.is(updatedEmployee.getAge())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName",
                        CoreMatchers.is(updatedEmployee.getLastName())));
    }

    @Test
    @Order(7)
    @DisplayName("Integration test for Update Employee (Failed Case)")
    void givenEmployeeObject_whenUpdate_thenReturn404() throws Exception {
        //given - precondition or setup
        long employeeId = 1L;
        Employee employee = Employee.builder()
                .firstName("Lucas")
                .lastName("Peixoto")
                .email("lspeixotodev@gmail.com")
                .age(31)
                .build();

        employeeRepository.save(employee);

        Employee updatedEmployee = Employee.builder()
                .id(employeeId)
                .firstName("Lucas")
                .lastName("Peixoto Fernandes")
                .email("lspeixotodev@gmail.com")
                .age(32)
                .build();

        String doesNotExistsErrorMessage = "Employee Does not exists with given Id: " + employeeId;

        //when - action or the behaviour we`re testing
        ResultActions response = mockMvc.perform(
                MockMvcRequestBuilders.put("/api/employees", employee)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEmployee))
        );

        //then - verify the result or output using assert statements
        response
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is(doesNotExistsErrorMessage)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @Order(8)
    @DisplayName("Integration test for Delete Employee (Success Case)")
    void givenEmployeeObject_whenDeleteById_thenReturnUpdatedEmployee() throws Exception {
        //given - precondition or setup

        Employee employee = Employee.builder()
                .firstName("Lucas")
                .lastName("Peixoto")
                .email("lspeixotodev@gmail.com")
                .age(31)
                .build();

        Employee savedEmployee = employeeRepository.save(employee);

        //when - action or the behaviour we`re testing
        ResultActions response = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/employees", savedEmployee)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedEmployee))
        );

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
    @Order(9)
    @DisplayName("Integration test for Delete Employee (Failed Case)")
    void givenEmployeeObject_whenDeleteById_thenReturn404() throws Exception {
        //given - precondition or setup
        long employeeId = 1L;
        Employee employee = Employee.builder()
                .firstName("Lucas")
                .lastName("Peixoto")
                .email("lspeixotodev@gmail.com")
                .age(31)
                .build();

        Employee deletedEmployee = Employee.builder()
                .id(employeeId)
                .firstName("Lucas")
                .lastName("Peixoto Fernandes")
                .email("lspeixotodev@gmail.com")
                .age(32)
                .build();

        employeeRepository.save(employee);

        String doesNotExistsErrorMessage = "Employee Does not exists with given Id: " + employeeId;

        //when - action or the behaviour we`re testing
        ResultActions response = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/employees", deletedEmployee)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deletedEmployee))
        );

        //then - verify the result or output using assert statements
        response
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is(doesNotExistsErrorMessage)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }
}
