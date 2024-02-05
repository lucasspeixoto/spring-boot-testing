package com.lspeixotodev.springboottesting.repository;

import com.lspeixotodev.springboottesting.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    @Query("select e from Employee e where e.firstName = ?1 and e.lastName = ?2")
    Optional<Employee> findByFirstAndLastName(String firstName, String lastName);

    @Query("select e from Employee e where e.firstName = ?1 and e.lastName = ?2")
    Optional<Employee> findByFirstAndLastNameNamed(@Param("firstName") String firstName, @Param("firstName") String lastName);

    @Query(value = "select * from employees e where e.age =:age", nativeQuery = true)
    Optional<Employee> findByAge(@Param("age") int age);

}
