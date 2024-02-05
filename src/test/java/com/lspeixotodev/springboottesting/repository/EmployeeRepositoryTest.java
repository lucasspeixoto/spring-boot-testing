package com.lspeixotodev.springboottesting.repository;

import com.lspeixotodev.springboottesting.model.Employee;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Employee Repository Test")
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;

    @BeforeEach
    public void setup() {
        employee = Employee.builder()
                .firstName("Lucas")
                .lastName("Peixoto")
                .email("lspeixotodev@gmail.com")
                .age(32)
                .build();
    }

    @Test
    @DisplayName("JUnit test for get saved Employee operation!")
    @Order(0)
    public void givenEmployeeObject_whenSave_thenReturnSavedEmployee() {
        //when - action or the behaviour we`re testing
        Employee savedEmployee = employeeRepository.save(employee);

        //then - verify the output
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getId()).isGreaterThan(0);

    }

    @Test
    @DisplayName("JUnit test for get all employees operation!")
    @Order(1)
    public void givenEmployeeList_whenFindAll_thenReturnSavedEmployeeList() {
        //given - precondition or setup
        employeeRepository.save(employee);

        Employee employee2 = Employee.builder()
                .firstName("Liana")
                .lastName("Fernandes")
                .email("lianacgf@gmail.com")
                .age(29)
                .build();

        employeeRepository.save(employee2);

        //when - action or the behaviour we`re testing
        List<Employee> employeesList = employeeRepository.findAll();

        //then - verify the output
        assertThat(employeesList).isNotNull();
        assertThat(employeesList.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("JUnit test for get a employee by Id operation!")
    @Order(2)
    public void givenEmployeeObject_whenFindById_thenReturnEmployeeObject() {
        //given - precondition or setup
        employeeRepository.save(employee);

        //when - action or the behaviour we`re testing
        Employee findEmployee = employeeRepository.findById(employee.getId()).get();

        //then - verify the output
        assertThat(findEmployee).isNotNull();
        assertThat(findEmployee.getFirstName()).isEqualTo("Lucas");
        assertThat(findEmployee.getLastName()).isEqualTo("Peixoto");
        assertThat(findEmployee.getAge()).isEqualTo(32);
        assertThat(findEmployee.getEmail()).isEqualTo("lspeixotodev@gmail.com");

    }

    @Test
    @DisplayName("JUnit test for get a employee by Email operation!")
    @Order(3)
    public void givenEmployeeObject_whenFindByEmail_thenReturnEmployeeObject() {

        //given - precondition or setup
        employeeRepository.save(employee);

        //when - action or the behaviour we`re testing
        Employee findEmployee = employeeRepository.findByEmail(employee.getEmail()).get();

        //then - verify the output
        assertThat(findEmployee).isNotNull();
        assertThat(findEmployee.getFirstName()).isEqualTo("Lucas");
        assertThat(findEmployee.getAge()).isEqualTo(32);
        assertThat(findEmployee.getLastName()).isEqualTo("Peixoto");
    }

    @Test
    @DisplayName("JUnit test for update a employee operation!")
    @Order(4)
    public void givenEmployeeObject_whenUpdateEmployee_thenReturnUpdatedEmployee() {

        //given - precondition or setup
        employeeRepository.save(employee);

        //when - action or the behaviour we`re testing
        Employee savedEmployee = employeeRepository.findById(employee.getId()).get();
        savedEmployee.setLastName("Fernandes");
        savedEmployee.setAge(33);

        //then - verify the output
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getFirstName()).isEqualTo("Lucas");
        assertThat(savedEmployee.getAge()).isEqualTo(33);
        assertThat(savedEmployee.getLastName()).isEqualTo("Fernandes");
    }

    @Test
    @DisplayName("JUnit test for delete a employee operation!")
    @Order(5)
    public void givenEmployeeObject_whenDelete_thenReturnDeletedEmployeeObject() {

        //given - precondition or setup
        employeeRepository.save(employee);

        //when - action or the behaviour we`re testing
        employeeRepository.delete(employee); //Option 1
        employeeRepository.deleteById(employee.getId()); //Option 2

        List<Employee> employees = employeeRepository.findAll();
        Optional<Employee> employeedOptional = employeeRepository.findById(employee.getId());

        //then - verify the output
        assertThat(employees.size()).isEqualTo(0);
        assertThat(employeedOptional).isEmpty();

    }

    @Test
    @DisplayName("JUnit test for find a employee by Find By FirstName and LastName operation!")
    @Order(6)
    public void givenEmployeeObject_whenFindByFirstAndLastName_thenReturnEmployeeObject() {

        //given - precondition or setup
        employeeRepository.save(employee);

        //when - action or the behaviour we`re testing
        Optional<Employee> employeedOptional = employeeRepository.findByFirstAndLastName(employee.getFirstName(), employee.getLastName());

        //then - verify the output
        assertThat(employeedOptional).isNotNull();

        if (employeedOptional.isPresent()) {
            Employee selectedEmployee = employeedOptional.get();
            assertThat(selectedEmployee.getFirstName()).isEqualTo("Lucas");
            assertThat(selectedEmployee.getLastName()).isEqualTo("Peixoto");
            assertThat(selectedEmployee.getAge()).isEqualTo(32);
        }
    }

    @Test
    @DisplayName("JUnit test for find a employee by Find By FirstName and LastName Named operation!")
    @Order(7)
    public void givenEmployeeObject_whenFindByFirstAndLastNameNamed_thenReturnEmployeeObject() {
        //given - precondition or setup
        employeeRepository.save(employee);

        //when - action or the behaviour we`re testing
        Optional<Employee> employeedOptional = employeeRepository.findByFirstAndLastNameNamed(employee.getFirstName(), employee.getLastName());

        //then - verify the output
        assertThat(employeedOptional).isNotNull();

        if (employeedOptional.isPresent()) {
            Employee selectedEmployee = employeedOptional.get();
            assertThat(selectedEmployee.getFirstName()).isEqualTo("Lucas");
            assertThat(selectedEmployee.getLastName()).isEqualTo("Peixoto");
        }
    }

    @Test
    @DisplayName("JUnit test for find a employee by Find By age operation!")
    @Order(8)
    public void givenEmployeeObject_whenFindByAge_thenReturnEmployeeObject() {

        //given - precondition or setup
        employeeRepository.save(employee);

        //when - action or the behaviour we`re testing
        Optional<Employee> employeedOptional = employeeRepository.findByAge(employee.getAge());

        //then - verify the output
        assertThat(employeedOptional).isNotNull();

        if (employeedOptional.isPresent()) {
            Employee selectedEmployee = employeedOptional.get();
            assertThat(selectedEmployee.getFirstName()).isEqualTo("Lucas");
            assertThat(selectedEmployee.getLastName()).isEqualTo("Peixoto");
            assertThat(selectedEmployee.getAge()).isEqualTo(32);
        }
    }
}