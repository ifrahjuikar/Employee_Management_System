package com.springbootemployeedata.springbootemployeedata.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.springbootemployeedata.springbootemployeedata.Entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    // Custom query to retrieve a list of employees with their IDs and full names.
    @Query("SELECT e.id, e.fullname FROM Employee e")
    List<List<Object>> listEmployeesByUsingSqlQuery();

    // Custom query to find employees by their full name, ignoring case.
    @Query("SELECT e FROM Employee e WHERE lower(e.fullname) LIKE lower(concat('%', ?1, '%'))")
    List<Employee> findByFullnameContainingIgnoreCase1(String filter);

    //custom finder method to find employee detault by full name
    List<Employee> findByFullnameContainingIgnoreCase(String fullname);

    // Custom query to retrieve detailed information about an employee by their ID.
    @Query("SELECT e.fullname AS FullNameofEmployee, e.rank.rankDesc AS RankDescription, e.department.deptName AS departmentName, e.reportingOfficer.fullname AS SupervisorName FROM Employee e WHERE e.empId = :empid")
    Map<String, Object> findEmployeeDetailsById(@Param("empid") int empid);
}
