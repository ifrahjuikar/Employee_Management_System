package com.springbootemployeedata.springbootemployeedata.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.lowagie.text.DocumentException;
import com.springbootemployeedata.springbootemployeedata.Entity.Employee;
import com.springbootemployeedata.springbootemployeedata.Exception.EmployeeNotFoundException;
import com.springbootemployeedata.springbootemployeedata.service.EmployeeService;
import com.springbootemployeedata.springbootemployeedata.util.ApiLoggingUtility;
import com.springbootemployeedata.springbootemployeedata.util.HttpResponseHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller class for handling employee-related HTTP requests.
 */
@RestController

public class EmployeeRestController {

   @Autowired
   private ApiLoggingUtility apiLoggingUtility;

   private static final String USERNAME = "admin";
   private static final String PASSWORD = "1234";

   private static final Logger log = LoggerFactory.getLogger(EmployeeRestController.class);

   @Autowired
   private EmployeeService employeeService;

   @PostMapping("/Employee")
   public ResponseEntity<Object> addEmployee(@RequestBody Employee employee,
         @RequestHeader Map<String, String> headers, HttpServletRequest request, HttpServletResponse response) {
      log.debug("Requesting To add Employee");
      if (!authorize(headers)) {
         log.warn("Unauthorized access attempt with headers: {}", headers);
         // apiLoggingUtility.logApiRequest(request, response, null, null);
         return HttpResponseHandler.generateResponse("Unauthorized Access", HttpStatus.UNAUTHORIZED.value(),
               HttpStatus.UNAUTHORIZED, null);
      }
      log.info("Authorization Sucessful");
      try {
         if (isRequestBodyEmpty(employee)) {
            log.warn("Empty request body received");
            return HttpResponseHandler.generateResponse("Empty request body", HttpStatus.BAD_REQUEST.value(),
                  HttpStatus.BAD_REQUEST, null);
         } else {
            apiLoggingUtility.logApiRequest(request, response, employee, null);
            employeeService.addEmployee(employee);
            log.info("Employee added successfully: {}", employee);
            return HttpResponseHandler.generateResponse("Success", HttpStatus.OK.value(), HttpStatus.OK, employee);
         }
      } catch (Exception e) {
         log.error("Error adding employee: ", e);
         return HttpResponseHandler.generateResponse("Internal Server Error",
               HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR, null);
      }
   }

   /**
    * Retrieves a list of all employees with their ID and full name.
    *
    * @param headers The request headers containing authorization information.
    * @return ResponseEntity containing the ApiResponse with the list of employees
    *         or an error message.
    */
   @GetMapping("/Employees/v1")
   public ResponseEntity<?> listAllEmployeesIdAndFullName(@RequestHeader Map<String, String> headers) {
      log.debug("Received request to list all employees with id and fullname (v1)");
      if (!authorize(headers)) {
         log.warn("Unauthorized access attempt with headers: {}", headers);
         return HttpResponseHandler.generateResponse("Unauthorized Access", HttpStatus.UNAUTHORIZED.value(),
               HttpStatus.UNAUTHORIZED, null);
      }
      try {
         List<List<Object>> employeeList = employeeService.listAllEmployees();
         log.info("List of employees retrieved successfully");
         return HttpResponseHandler.generateResponse("success", HttpStatus.OK.value(), HttpStatus.OK, employeeList);
      } catch (Exception e) {
         log.error("Error retrieving list of employees: ", e);
         return HttpResponseHandler.generateResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value(),
               HttpStatus.INTERNAL_SERVER_ERROR, null);
      }
   }

   /**
    * Retrieves a list of all employees.
    * 
    * @return ResponseEntity containing the list of employees.
    */

   @GetMapping("/Employees/v2")
   public ResponseEntity<?> listAllEmployeesIdAndFullName1(@RequestHeader Map<String, String> headers) {
      log.debug("Received request to list all employees with id and fullname (v2)");
      if (!authorize(headers)) {
         log.warn("Unauthorized access attempt with headers: {}", headers);
         return HttpResponseHandler.generateResponse("Unauthorized Access", HttpStatus.UNAUTHORIZED.value(),
               HttpStatus.UNAUTHORIZED, null);
      }
      try {
         List<List<Object>> employeeList = employeeService.listEmployeesByUsingSqlQuery();
         log.info("List of employees retrieved successfully");
         return HttpResponseHandler.generateResponse("Success", HttpStatus.OK.value(), HttpStatus.OK, employeeList);
      } catch (Exception e) {
         log.error("Error retrieving list of employees: ", e);
         return HttpResponseHandler.generateResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value(),
               HttpStatus.INTERNAL_SERVER_ERROR, null);
      }
   }

   /**
    * Retrieves a all details of employee by using full name.
    * 
    * @return ResponseEntity containing detail of employee.
    */
   // Assignment 9
   @GetMapping("/Employee/v1")
   public ResponseEntity<?> listEmployeesFilteredByFullNameV1(@RequestParam(required = false) String filter,
         @RequestHeader Map<String, String> headers) {
      log.debug("Received request to filter employees filter: {}", filter);
      if (!authorize(headers)) {
         log.warn("Unauthorized access attempt with headers: {}", headers);
         return HttpResponseHandler.generateResponse("Unauthorized Access", HttpStatus.UNAUTHORIZED.value(),
               HttpStatus.UNAUTHORIZED, null);
      }
      try {
         List<Employee> filteredEmployees = employeeService.findByFullnameContainingIgnoreCase(filter);
         log.info("Filtered employees retrieved successfully with filter: {}", filter);
         return HttpResponseHandler.generateResponse("Success", HttpStatus.OK.value(), HttpStatus.OK,
               filteredEmployees);
      } catch (EmployeeNotFoundException ex) {
         log.error("Employee not found with filter: {}", filter);
         return HttpResponseHandler.generateResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(),
               HttpStatus.NOT_FOUND, null);
      } catch (Exception e) {
         log.error("Error occurred while searching for employees by full name (v1): ", e);
         return HttpResponseHandler.generateResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value(),
               HttpStatus.INTERNAL_SERVER_ERROR, null);
      }
   }

   /**
    * Retrieves a all details of employee by using full name.
    * 
    * @return ResponseEntity containing detail of employee.
    */
   @GetMapping("/Employee/v2/{filter}")
   public ResponseEntity<?> listEmployeesFilteredByFullNameV2(@PathVariable String filter,
         @RequestHeader Map<String, String> headers) {
      log.debug("Received request to filter employees by filter: {}", filter);
      if (!authorize(headers)) {
         log.warn("Unauthorized access attempt with headers: {}", headers);
         return HttpResponseHandler.generateResponse("Unauthorized Access", HttpStatus.UNAUTHORIZED.value(),
               HttpStatus.UNAUTHORIZED, null);
      }
      try {
         List<Employee> filteredEmployees1 = employeeService.findByFullnameContainingIgnoreCase(filter);
         log.info("Filtered employees retrieved successfully with filter: {}", filter);

         return HttpResponseHandler.generateResponse("Success", HttpStatus.OK.value(), HttpStatus.OK,
               filteredEmployees1);
      } catch (EmployeeNotFoundException ex) {
         log.error("Employee not found with filter: {}", filter);

         return HttpResponseHandler.generateResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(),
               HttpStatus.NOT_FOUND, null);
      } catch (Exception e) {
         log.error("Error occurred while searching for employees by full name (v2): ", e);
         return HttpResponseHandler.generateResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value(),
               HttpStatus.INTERNAL_SERVER_ERROR, null);
      }
   }

   /**
    * Retrieves a all details of employee by using full name.
    * 
    * @return ResponseEntity containing detail of employee.
    */
   @GetMapping("/Employee/v3/{fullName}")
   public ResponseEntity<?> searchEmployees(@PathVariable String fullName,
         @RequestHeader Map<String, String> headers) {
      log.debug("Received request to filter employees by fullname");
      if (!authorize(headers)) {
         log.warn("Unauthorized access attempt with headers: {}", headers);
         return HttpResponseHandler.generateResponse("Unauthorized Access", HttpStatus.UNAUTHORIZED.value(),
               HttpStatus.UNAUTHORIZED, null);
      }
      try {
         List<Employee> employees = employeeService.findEmployeesByFullName(fullName);
         log.info("Filtered employees retrieved successfully with filter: fullname");

         return HttpResponseHandler.generateResponse("Success", HttpStatus.OK.value(), HttpStatus.OK, employees);
      } catch (EmployeeNotFoundException ex) {
         log.error("Employee not found with filter: {}", fullName);

         return HttpResponseHandler.generateResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(),
               HttpStatus.NOT_FOUND, null);
      } catch (Exception e) {
         log.error("Error occurred while searching for employees by full name (v3): ", e);
         return HttpResponseHandler.generateResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value(),
               HttpStatus.INTERNAL_SERVER_ERROR, null);
      }
   }

   /**
    * Retrieves details of an employee by their ID.
    * 
    * @param empid The ID of the employee.
    * @return ResponseEntity containing the employee details.
    */
   // Assignment-10
   @GetMapping("/Employeee/{empid}")
   public ResponseEntity<Object> getEmployeeDetails(@PathVariable int empid,
         @RequestHeader Map<String, String> headers) {
      log.debug("Received request to get employee details for ID: {}", empid);
      if (!authorize(headers)) {
         log.warn("Unauthorized access attempt with headers: {}", headers);
         return HttpResponseHandler.generateResponse("Unauthorized Access", HttpStatus.UNAUTHORIZED.value(),
               HttpStatus.UNAUTHORIZED, null);
      }
      try {
         Object result = employeeService.getEmployeeDetails(empid);
         log.info("Employee details retrieved successfully for ID: {}", empid);
         if (result instanceof Map) {
            return HttpResponseHandler.generateResponse("Success", HttpStatus.OK.value(), HttpStatus.OK, result);
         } else {
            log.error("Unexpected result type for employee details with ID: {}", empid);
            return HttpResponseHandler.generateResponse("Unexpected result type",
                  HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR,
                  null);
         }
      } catch (EmployeeNotFoundException ex) {
         log.error("Employee not found with ID: {}", empid);
         return HttpResponseHandler.generateResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(),
               HttpStatus.NOT_FOUND, null);
      } catch (Exception e) {
         log.error("Error occurred while getting employee details for ID: {}", empid, e);
         return HttpResponseHandler.generateResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value(),
               HttpStatus.INTERNAL_SERVER_ERROR, null);
      }
   }

   /**
    * Updates details of an employee.
    *
    * @param empId   The ID of the employee to update.
    * @param request The updated details of the employee.
    * @return ResponseEntity containing the updated employee details or error
    *         message.
    */
   // Assignment-11
   @PutMapping("/Employee/{empId}")
   public ResponseEntity<Object> updateEmployeeDetails(@PathVariable int empId, @RequestBody Employee requestEmployee,
         @RequestHeader Map<String, String> headers, HttpServletRequest request, HttpServletResponse response) {
      MDC.put("userId", String.valueOf(empId));
      log.debug("Received request to update employee details for ID: {}", empId);
      if (!authorize(headers)) {
         log.warn("Unauthorized access attempt with headers: {}", headers);
         return HttpResponseHandler.generateResponse("Unauthorized Access", HttpStatus.UNAUTHORIZED.value(),
               HttpStatus.UNAUTHORIZED, null);
      }

      if (isRequestBodyEmpty(requestEmployee)) {
         log.warn("Request body is empty for update request for employee ID: {}", empId);
         return HttpResponseHandler.generateResponse("Request body cannot be empty", HttpStatus.BAD_REQUEST.value(),
               HttpStatus.BAD_REQUEST, null);
      } else if (requestEmployee.getFirstname() == null || requestEmployee.getFullname() == null) {
         log.warn("Required fields are missing in the request body for employee ID: {}", empId);
         return HttpResponseHandler.generateResponse("Firstname and fullname are required fields",
               HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, null);
      } else {
         try {
            apiLoggingUtility.logApiRequest(request, response, requestEmployee, null);

            Employee updatedEmployee = employeeService.updateEmployee(requestEmployee);
            log.info("Employee updated successfully for ID: {}", empId);
            return HttpResponseHandler.generateResponse("Success", HttpStatus.OK.value(), HttpStatus.OK,
                  updatedEmployee);
         } catch (EmployeeNotFoundException ex) {
            log.error("Employee not found with ID: {}", empId);
            return HttpResponseHandler.generateResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(),
                  HttpStatus.NOT_FOUND, null);
         } catch (Exception e) {
            log.error("Error occurred while updating employee details for ID: {}", empId, e);
            return HttpResponseHandler.generateResponse("Internal Server Error",
                  HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR,
                  null);
         } finally {
            MDC.clear(); // Clear MDC after the action is performed
         }
      }
   }

   /**
    * Deletes an employee by their ID.
    * 
    * @param empid The ID of the employee to delete.
    * @return ResponseEntity containing the status of the operation.
    */
   // Assignment-13
   @DeleteMapping("/Employee/{empid}")
   public ResponseEntity<?> deleteEmployee(@PathVariable int empid, @RequestHeader Map<String, String> headers) {
      log.info("Received request to delete employee with ID: {}", empid);
      if (!authorize(headers)) {
         log.warn("Unauthorized access attempt with headers: {}", headers);
         return HttpResponseHandler.generateResponse("Unauthorized Access", HttpStatus.UNAUTHORIZED.value(),
               HttpStatus.UNAUTHORIZED, null);
      }
      try {
         employeeService.deleteEmployee(empid);
         log.info("Employee deleted successfully with ID: {}", empid);
         return HttpResponseHandler.generateResponse("Deleted", HttpStatus.NO_CONTENT.value(), HttpStatus.NO_CONTENT,
               null);
      } catch (EmployeeNotFoundException ex) {
         log.error("Employee not found with ID: {}", empid);
         return HttpResponseHandler.generateResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(),
               HttpStatus.NOT_FOUND, null);
      } catch (Exception e) {
         log.error("Error occurred while deleting employee with ID: {}", empid, e);
         return HttpResponseHandler.generateResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value(),
               HttpStatus.INTERNAL_SERVER_ERROR, null);
      }
   }

   /**
    * Retrieves specific details of an employee by their ID.
    * 
    * @param empid The ID of the employee.
    * @return ResponseEntity containing the employee details.
    */
   @GetMapping("/Employee/{empid}")
   public ResponseEntity<?> getEmployeeById(@PathVariable Integer empid, @RequestHeader Map<String, String> headers) {
      log.debug("Received request to get employee by ID: {}", empid);
      if (!authorize(headers)) {
         log.warn("Unauthorized access attempt with headers: {}", headers);
         return HttpResponseHandler.generateResponse("Unauthorized Access", HttpStatus.UNAUTHORIZED.value(),
               HttpStatus.UNAUTHORIZED, null);
      }
      try {
         Employee employee = employeeService.getOneEmployeeById(empid);
         log.info("Employee retrieved successfully with ID: {}", empid);
         return HttpResponseHandler.generateResponse("Success", HttpStatus.OK.value(), HttpStatus.OK, employee);
      } catch (EmployeeNotFoundException ex) {
         log.error("Employee not found with ID: {}", empid);
         return HttpResponseHandler.generateResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(),
               HttpStatus.NOT_FOUND, null);
      } catch (Exception e) {
         log.error("Error occurred while getting employee by ID: {}", empid, e);
         return HttpResponseHandler.generateResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value(),
               HttpStatus.INTERNAL_SERVER_ERROR, null);
      }
   }

   @GetMapping("/Pdf")
   public void generatePdfFile(HttpServletResponse response) throws DocumentException, IOException {
      // setting up your response type - 1
      response.setContentType("application/pdf");

      DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD:HH:MM:SS");
      String currentDateTime = dateFormat.format(new Date());

      String headerkey = "Content-Disposition";
      String headervalue = "attachment; filename=Employee" + currentDateTime + ".pdf";

      response.setHeader(headerkey, headervalue);
      // reading all the employee
      List<Employee> listOfEmployee = employeeService.getAllEmployees();
      // object of pdf generator
      // passing list of employee and response object
      employeeService.generateEmployeePdf(listOfEmployee, response);
   }

   @GetMapping("/Excel")
   public void generateExcelFile(HttpServletResponse response) throws IOException {
      // Set content type and filename in response headers
      response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
      String currentDateTime = dateFormat.format(new Date());
      String headerKey = "Content-Disposition";
      String headerValue = "attachment; filename=Employee" + currentDateTime + ".xlsx";
      response.setHeader(headerKey, headerValue);

      // Retrieve list of employees
      List<Employee> listOfEmployee = employeeService.getAllEmployees();

      // Generate Excel file and send it in the response
      employeeService.generateEmployeeExcel(listOfEmployee, response);
   }

   /**
    * Retrieves all employee detail.
    * 
    * @param empid The ID of the employee.
    * @return ResponseEntity containing the employee details.
    */
   @GetMapping("/Employees")
   public List<Employee> getAllEmployee() {
      log.info("Received request to get all employees");
      return employeeService.getAllEmployees();
   }

   /**
    * Checks if the request body is empty.
    * 
    * @param request The request body.
    * @return True if the request body is empty, otherwise false.
    */
   private boolean isRequestBodyEmpty(Employee request) {
      // Check if all fields in the request object are null
      return request.getFirstname() == null && request.getFullname() == null && request.getDob() == null
            && request.getDoj() == null && request.getSalary() == 0 && request.getDepartment() == null
            && request.getRank() == null && request.getReportingOfficer() == null
            && request.getClientRequestId() == null;
   }

   private boolean authorize(Map<String, String> headers) {
      String authUsername = headers.get("username");
      String authPassword = headers.get("password");
      return USERNAME.equals(authUsername) && PASSWORD.equals(authPassword);
   }
}
