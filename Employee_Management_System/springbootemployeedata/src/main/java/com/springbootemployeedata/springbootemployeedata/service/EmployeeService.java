package com.springbootemployeedata.springbootemployeedata.service;

import java.io.IOException;
import java.util.List;

import com.lowagie.text.DocumentException;
import com.springbootemployeedata.springbootemployeedata.Entity.Employee;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Service interface for managing Employee entities.
 */
public interface EmployeeService {

    /**
     * Adds a new employee.
     *
     * @param employee The employee to add.
     */
    public void addEmployee(Employee employee);

    /**
     * Retrieves a list of all employees.
     *
     * @return A list of all employees.
     */
    public List<List<Object>> listAllEmployees();

    /**
     * Retrieves a list of employees using a SQL query.
     *
     * @return A list of employees.
     */
    public List<List<Object>> listEmployeesByUsingSqlQuery();

    /**
     * Finds employees by full name, ignoring case.
     *
     * @param filter The string to search for in employee full names.
     * @return A list of matching employees.
     */
    public List<Employee> findByFullnameContainingIgnoreCase(String filter);

    public List<Employee> findEmployeesByFullName(String fullName);
    
    /**
     * Retrieves details of an employee by employee ID.
     *
     * @param empid The ID of the employee.
     * @return Details of the employee.
     */
    public Object getEmployeeDetails(int empid);

    /**
     * Updates an employee's information.
     *
     * @param empid  The ID of the employee to update.
     * @param emp    The updated employee information.
     * @return The updated employee.
     */
    public Employee updateEmployee(Employee emp);
    /**
     * Deletes an employee.
     *
     * @param empid The ID of the employee to delete.
     * @return The deleted employee.
     */
    public Employee deleteEmployee(int empid);

    /**
     * Retrieves an employee by employee ID.
     *
     * @param empid The ID of the employee.
     * @return The employee.
     */
    public Employee getOneEmployeeById(Integer empid);

    public void generateEmployeePdf(List<Employee> empList, HttpServletResponse response) throws DocumentException, IOException;


    public void generateEmployeeExcel(List<Employee> empList, HttpServletResponse response) throws IOException;

    /**
     * Retrieves a list of all employees.
     *
     * @return A list of all employees.
     */
    public List<Employee> getAllEmployees();
}
