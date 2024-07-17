package com.springbootemployeedata.springbootemployeedata.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lowagie.text.DocumentException;
import com.springbootemployeedata.springbootemployeedata.Entity.Employee;
import com.springbootemployeedata.springbootemployeedata.Exception.ApiResponse;
import com.springbootemployeedata.springbootemployeedata.service.EmployeeService;
import com.springbootemployeedata.springbootemployeedata.util.LoggingWarnUtility;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class EmployeeController {

        @Autowired
        private EmployeeService employeeService;
        // Authorization credentials
        private static final String userName = "ifrah";
        private static final String password = "1234";

        // Logger for logging messages
        private static final Logger log = LoggerFactory.getLogger(EmployeeRestController.class);

        /**
         * Endpoint to add a new employee.
         * 
         * @param employee The employee object to add.
         * @param headers  The request headers containing authorization details.
         * @return ResponseEntity containing the result of the operation.
         */
        @PostMapping("/employee")
        public ResponseEntity<ApiResponse> addEmployee(@RequestBody Employee employee,
                        @RequestHeader Map<String, String> headers) {
                ResponseEntity<ApiResponse> responseEntity;
                log.debug("Requesting To add Employee");
                
                // Check authorization
                if (!isAuthorize(headers)) {
                        LoggingWarnUtility.warnLogging("unauthorized.access",headers.get("username"));
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Error", HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access",
                                                        null),
                                        HttpStatus.UNAUTHORIZED);
                } else if (isRequestBodyEmpty(employee)) {
                        // Check if request body is empty
                        LoggingWarnUtility.warnLogging("empty.request.body");

                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Error", HttpStatus.BAD_REQUEST.value(), "Empty request body",
                                                        null),
                                        HttpStatus.BAD_REQUEST);
                } else {
                        // Add employee
                        employeeService.addEmployee(employee);
                        log.info("Employee added successfully: {}", employee);
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Success", HttpStatus.OK.value(), "Success", employee),
                                        HttpStatus.OK);
                }
                return responseEntity;
        }

        /**
         * Retrieves a list of all employees with their ID and full name.
         *
         * @param headers The request headers containing authorization information.
         * @return ResponseEntity containing the ApiResponse with the list of
         *         employees or an error message.
         */
        @GetMapping("/employees/v1")
        public ResponseEntity<?> listAllEmployeesIdAndFullName(@RequestHeader Map<String, String> headers) {
                // Log a debug message indicating the request to list all employees with id and
                // fullname
                ResponseEntity<?> responseEntity;
                log.debug("Received request to list all employees with id and fullname (v1)");
                // Check authorization based on request headers
                if (!isAuthorize(headers)) {
                        // Check authorization based on request headers
                        LoggingWarnUtility.warnLogging("unauthorized.access",headers.get("username"));
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Error", HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access",
                                                        null),
                                        HttpStatus.UNAUTHORIZED);
                } else {
                        log.info("Authorization Successful");
                        // Retrieve the list of all employees from the service layer
                        List<List<Object>> employeeList = employeeService.listAllEmployees();
                        log.info("List of employees retrieved successfully");
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Success", HttpStatus.OK.value(),
                                                        "List of employee retrived suceesfully", employeeList),
                                        HttpStatus.OK);
                }
                return responseEntity;
        }

        /**
         * Retrieves a list of all employees with their ID and full name using SQL
         * query.
         *
         * @param headers The request headers containing authorization information.
         * @return ResponseEntity containing the ApiResponse with the list of employees
         *         or an error message.
         */
        @GetMapping("/employees/v2")
        public ResponseEntity<?> listAllEmployeesIdAndFullName1(@RequestHeader Map<String, String> headers) {
                // Log a debug message indicating the request to list all employees with id and
                // fullname (v2)
                ResponseEntity<?> responseEntity;
                log.debug("Received request to list all employees with id and fullname (v2)");

                // Check authorization based on request headers
                if (!isAuthorize(headers)) {
                        // Log a warning message for unauthorized access attempt
                        LoggingWarnUtility.warnLogging("unauthorized.access",headers.get("username"));
                        // Return a ResponseEntity with an error ApiResponse and UNAUTHORIZED status
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Error", HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access",
                                                        null),
                                        HttpStatus.UNAUTHORIZED);
                } else {

                        // Log an info message indicating successful authorization
                        log.info("Authorization Successful");

                        // Retrieve the list of employees using a SQL query from the service layer
                        List<List<Object>> employeeList = employeeService.listEmployeesByUsingSqlQuery();

                        // Log an info message indicating successful retrieval of the employee list
                        log.info("List of employees retrieved successfully");

                        // Return a ResponseEntity with a success ApiResponse containing the employee
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Success", HttpStatus.OK.value(), "Success", employeeList),
                                        HttpStatus.OK);
                }
                return responseEntity;
        }

        /**
         * Retrieves a list of employees filtered by full name.
         *
         * @param filter  The filter string to apply on full name (optional).
         * @param headers The request headers containing authorization information.
         * @return ResponseEntity containing the ApiResponse with the filtered list of
         *         employees or an error message.
         */
        @GetMapping("/employee/v1")
        public ResponseEntity<?> listEmployeesFilteredByFullNameV1(
                        @RequestParam(required = false) String filter,
                        @RequestHeader Map<String, String> headers) {
                // Log a debug message indicating the request to filter employees with a
                // specific filter
                ResponseEntity<?> responseEntity;
                log.debug("Received request to filter employees filter: {}", filter);

                // Check authorization based on request headers
                if (!isAuthorize(headers)) {
                        // Log a warning message for unauthorized access attempt
                        LoggingWarnUtility.warnLogging("unauthorized.access",headers.get("username"));
                        // Return a ResponseEntity with an error ApiResponse and UNAUTHORIZED status
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Error", HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access",
                                                        null),
                                        HttpStatus.UNAUTHORIZED);
                } else {
                        log.info("Authorization Successful");

                        // Retrieve the list of employees filtered by full name from the service layer
                        List<Employee> filteredEmployees = employeeService.findByFullnameContainingIgnoreCase(filter);

                        // Log an info message indicating successful retrieval of the filtered employee
                        // list
                        log.info("Filtered employees retrieved successfully with filter: {}", filter);

                        // Return a ResponseEntity with a success ApiResponse containing the filtered
                        // employee list and OK status
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Success", HttpStatus.OK.value(), "Success", filteredEmployees),
                                        HttpStatus.OK);
                }
                return responseEntity;
        }

        /**
         * Retrieves a list of employees filtered by full name.
         *
         * @param filter  The filter string to apply on full name.
         * @param headers The request headers containing authorization information.
         * @return ResponseEntity containing the ApiResponse with the filtered list of
         *         employees or an error message.
         */
        @GetMapping("/employee/v2/{filter}")
        public ResponseEntity<?> listEmployeesFilteredByFullNameV2(@PathVariable String filter,
                        @RequestHeader Map<String, String> headers) {
                // Log a debug message indicating the request to filter employees with a
                // specific filter
                ResponseEntity<?> responseEntity;
                log.debug("Received request to filter employees by filter: {}", filter);

                // Check authorization based on request headers
                if (!isAuthorize(headers)) {
                        // Log a warning message for unauthorized access attempt
                        LoggingWarnUtility.warnLogging("unauthorized.access",headers.get("username"));
                        // Return a ResponseEntity with an error ApiResponse and UNAUTHORIZED status
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Error", HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access",
                                                        null),
                                        HttpStatus.UNAUTHORIZED);
                } else {
                        log.info("Authorization Successful");

                        // Retrieve the list of employees filtered by full name from the service layer
                        List<Employee> filteredEmployees1 = employeeService.findByFullnameContainingIgnoreCase(filter);

                        // Log an info message indicating successful retrieval of the filtered employee
                        // list
                        log.info("Filtered employees retrieved successfully with filter: {}", filter);

                        // Return a ResponseEntity with a success ApiResponse containing the filtered
                        // employee list and OK status
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Success", HttpStatus.OK.value(), "Success",
                                                        filteredEmployees1),
                                        HttpStatus.OK);
                }
                return responseEntity;
        }

        /**
         * Retrieves a list of employees filtered by full name.
         *
         * @param fullName The full name to search for.
         * @param headers  The request headers containing authorization information.
         * @return ResponseEntity containing the ApiResponse with the filtered list of
         *         employees or an error message.
         */
        @GetMapping("/employee/v3/{fullName}")
        public ResponseEntity<?> searchEmployees(@PathVariable String fullName,
                        @RequestHeader Map<String, String> headers) {
                // Log a debug message indicating the request to filter employees by full name
                ResponseEntity<?> responseEntity;
                log.debug("Received request to filter employees by fullname");

                // Check authorization based on request headers
                if (!isAuthorize(headers)) {
                        // Log a warning message for unauthorized access attempt
                        LoggingWarnUtility.warnLogging("unauthorized.access",headers.get("username"));
                        // Return a ResponseEntity with an error ApiResponse and UNAUTHORIZED status
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Error", HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access",
                                                        null),
                                        HttpStatus.UNAUTHORIZED);
                } else {
                        log.info("Authorization Successful");

                        // Retrieve the list of employees filtered by full name from the service layer
                        List<Employee> employees = employeeService.findEmployeesByFullName(fullName);

                        // Log an info message indicating successful retrieval of the filtered employee
                        // list
                        log.info("Filtered employees retrieved successfully with filter0: ", fullName);

                        // Return a ResponseEntity with a success ApiResponse containing the filtered
                        // employee list and OK status
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Success", HttpStatus.OK.value(), "Success", employees),
                                        HttpStatus.OK);
                }
                return responseEntity;
        }

        /**
         * Retrieves details of an employee by their ID.
         *
         * @param empid   The ID of the employee to retrieve details for.
         * @param headers The request headers containing authorization information.
         * @return ResponseEntity containing the ApiResponse with the employee details
         *         or an error message.
         */
        @GetMapping("/employee/{empid}")
        public ResponseEntity<?> getEmployeeDetails(@PathVariable int empid,
                        @RequestHeader Map<String, String> headers) {
                ResponseEntity<?> responseEntity;

                // Log a debug message indicating the request to get employee details by ID
                log.debug("Received request to get employee details for ID: {}", empid);

                // Check authorization based on request headers
                if (!isAuthorize(headers)) {
                        // Log a warning message for unauthorized access attempt
                        LoggingWarnUtility.warnLogging("unauthorized.access",headers.get("username"));
                        // Return a ResponseEntity with an error ApiResponse and UNAUTHORIZED status
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Error", HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access",
                                                        null),
                                        HttpStatus.UNAUTHORIZED);
                } else {
                        log.info("Authorization Successful");

                        // Retrieve the details of the employee with the specified ID from the service
                        // layer
                        Object result = employeeService.getEmployeeDetails(empid);

                        // Log an info message indicating successful retrieval of employee details
                        log.info("Employee details retrieved successfully for ID: {}", empid);

                        // Return a ResponseEntity with a success ApiResponse containing the employee
                        // details and OK status
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Success", HttpStatus.OK.value(), "Retrieved successfully",
                                                        result),
                                        HttpStatus.OK);
                }
                return responseEntity;
        }

        /**
         * Updates details of an employee by their ID.
         *
         * @param empId           The ID of the employee to update.
         * @param requestEmployee The updated details of the employee.
         * @param headers         The request headers containing authorization
         *                        information.
         * @return ResponseEntity containing the ApiResponse indicating success or
         *         failure of the update operation.
         */
        @PutMapping("/employee/{empId}")
        public ResponseEntity<?> updateEmployeeDetails(@PathVariable int empId,
                        @RequestBody Employee requestEmployee,
                        @RequestHeader Map<String, String> headers) {
                ResponseEntity<?> responseEntity;

                // Log a debug message indicating the request to update employee details by ID
                log.debug("Received request to update employee details for ID: {}", empId);

                // Check authorization based on request headers
                if (!isAuthorize(headers)) {
                        // Log a warning message for unauthorized access attempt
                        LoggingWarnUtility.warnLogging("unauthorized.access",headers.get("username"));
                        // Return a ResponseEntity with an error ApiResponse and UNAUTHORIZED status
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Error", HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access",
                                                        null),
                                        HttpStatus.UNAUTHORIZED);
                } else if (isRequestBodyEmpty(requestEmployee)) {
                        // Log a warning message for empty request body
                        LoggingWarnUtility.warnLogging("empty.request.body" , "for update request for employee ID " + empId);
                        // Return a ResponseEntity with an error ApiResponse and BAD_REQUEST status
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Error", HttpStatus.BAD_REQUEST.value(),
                                                        "Request body cannot be empty", null),
                                        HttpStatus.BAD_REQUEST);
                } else {
                        // Update the employee details using the provided request body
                        Employee updatedEmployee = employeeService.updateEmployee(requestEmployee);
                        // Log an info message indicating successful update of employee details
                        log.info("Employee updated successfully for ID: {}", empId);
                        // Return a ResponseEntity with a success ApiResponse containing the updated
                        // employee details and OK status
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Success", HttpStatus.OK.value(), "Update successfully",
                                                        updatedEmployee),
                                        HttpStatus.OK);
                }
                return responseEntity;
        }

        /**
         * Deletes an employee by their ID.
         *
         * @param empid   The ID of the employee to delete.
         * @param headers The request headers containing authorization information.
         * @return ResponseEntity containing the status of the operation.
         */
        // Assignment-13
        @DeleteMapping("/employee/{empid}")
        public ResponseEntity<?> deleteEmployee(@PathVariable int empid, @RequestHeader Map<String, String> headers) {
                ResponseEntity<?> responseEntity;

                // Log an info message indicating the request to delete employee by ID
                log.info("Received request to delete employee with ID: {}", empid);

                // Check authorization based on request headers
                if (!isAuthorize(headers)) {
                        // Log a warning message for unauthorized access attempt
                        LoggingWarnUtility.warnLogging("unauthorized.access",headers.get("username"));
                        // Return a ResponseEntity with an error ApiResponse and UNAUTHORIZED status
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Error", HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access",
                                                        null),
                                        HttpStatus.UNAUTHORIZED);
                } else {
                        log.info("Authorization Successful");

                        // Delete the employee with the specified ID
                        Employee deletedEmployee = employeeService.deleteEmployee(empid);
                        // Log an info message indicating successful deletion of employee
                        log.info("Employee deleted successfully with ID: {}", empid);
                        // Return a ResponseEntity with a success ApiResponse containing the deleted
                        // employee details and OK status
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Success", HttpStatus.NO_CONTENT.value(),
                                                        "Employee deleted Successfully",
                                                        deletedEmployee),
                                        HttpStatus.OK);
                }
                return responseEntity;
        }

        /**
         * Retrieves specific details of an employee by their ID.
         *
         * @param empid   The ID of the employee.
         * @param headers The request headers containing authorization information.
         * @return ResponseEntity containing the employee details.
         * 
          */

        @GetMapping("/emp/{empid}")
        public ResponseEntity<?> getEmployeeById(@PathVariable Integer empid,
                        @RequestHeader Map<String, String> headers) {
                ResponseEntity<?> responseEntity;
                // Log a debug message indicating the request to retrieve employee by ID
                log.debug("Received request to get employee by ID: {}", empid);

                // Check authorization based on request headers
                if (!isAuthorize(headers)) {
                        // Log a warning message for unauthorized access attempt
                        LoggingWarnUtility.warnLogging("unauthorized.access",headers.get("username"));
                        // Return a ResponseEntity with an error ApiResponse and UNAUTHORIZED status
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Error", HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access",
                                                        null),
                                        HttpStatus.UNAUTHORIZED);
                } else {
                        log.info("Authorization Successful");

                        // Retrieve the employee details for the specified ID
                        Employee employee = employeeService.getOneEmployeeById(empid);
                        // Log an info message indicating successful retrieval of employee details
                        log.info("Employee retrieved successfully with ID: {}", empid);
                        // Return a ResponseEntity with a success ApiResponse containing the employee
                        // details and OK status
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Success", HttpStatus.OK.value(),
                                                        "Employee with empid " + empid + " retrived succussfully",
                                                        employee),
                                        HttpStatus.OK);
                }
                return responseEntity;
        }

        /**
         * Generates a PDF file containing employee information and sends it as a
         * response.
         *
         * @param response The HttpServletResponse object to send the PDF file as a
         *                 response.
         * @throws DocumentException If an error occurs during PDF document processing.
         * @throws IOException       If an I/O error occurs while sending the response.
         */
        @GetMapping("/pdf")
        public ResponseEntity<?> generatePdfFile(HttpServletResponse response,
                        @RequestHeader Map<String, String> headers) throws DocumentException, IOException {
                ResponseEntity<?> responseEntity;
                log.info("Request for generating pdf sucessfully");

                // Check authorization based on request headers
                if (!isAuthorize(headers)) {
                        // Log a warning message for unauthorized access attempt
                        LoggingWarnUtility.warnLogging("unauthorized.access",headers.get("username"));
                        // Return a ResponseEntity with an error ApiResponse and UNAUTHORIZED status
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Error", HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access",
                                                        null),
                                        HttpStatus.UNAUTHORIZED);
                } else {
                        log.info("Authorization Sucessful");

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
                        log.info("Pdf generated sucessfully");

                        return ResponseEntity.ok().build(); // Return ResponseEntity with OK status
                }
                return responseEntity;
        }

        /**
         * Generates an Excel file containing employee information and sends it as a
         * response.
         *
         * @param response The HttpServletResponse object to send the Excel file as a
         *                 response.
         * @throws IOException If an I/O error occurs while sending the response.
         */
        @GetMapping("/excel")
        public ResponseEntity<?> generateExcelFile(HttpServletResponse response,
                        @RequestHeader Map<String, String> headers) throws IOException {
                ResponseEntity<?> responseEntity;
                log.debug("Request for generating excel sheet of employee detail");
                // Check authorization based on request headers
                if (!isAuthorize(headers)) {
                        // Log a warning message for unauthorized access attempt
                        LoggingWarnUtility.warnLogging("unauthorized.access",headers.get("username"));
                        // Return a ResponseEntity with an error ApiResponse and UNAUTHORIZED status
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Error", HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access",
                                                        null),
                                        HttpStatus.UNAUTHORIZED);
                } else {
                        log.info("Authorization Sucessful");

                        // Set the content type to Excel format
                        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

                        // Get the current date and time in a specific format to include in the filename
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                        String currentDateTime = dateFormat.format(new Date());

                        // Set the response header to define the file attachment with a unique filename
                        String headerKey = "Content-Disposition";
                        String headerValue = "attachment; filename=Employee" + currentDateTime + ".xlsx";
                        response.setHeader(headerKey, headerValue);

                        // Retrieve the list of employees
                        List<Employee> listOfEmployee = employeeService.getAllEmployees();

                        // Generate the Excel file containing employee information and send it as the
                        // response
                        employeeService.generateEmployeeExcel(listOfEmployee, response);
                        log.info("excel sheet generated sucessfully");
                        responseEntity = ResponseEntity.ok().build();
                }
                return responseEntity;
        }

        /**
         * Retrieves all employee detail.
         * 
         * @param empid The ID of the employee.
         * @return ResponseEntity containing the employee details.
         */
        @GetMapping("/employees")
        public ResponseEntity<?> getAllEmployee(@RequestHeader Map<String, String> headers) {
                ResponseEntity<?> responseEntity;
                log.info("Received request to get all employees");

                if (!isAuthorize(headers)) {
                        // Log a warning message for unauthorized access attempt
                        LoggingWarnUtility.warnLogging("unauthorized.access",headers.get("username"));
                        // Return a ResponseEntity with an error ApiResponse and UNAUTHORIZED status
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Error", HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access",
                                                        null),
                                        HttpStatus.UNAUTHORIZED);
                } else {
                        log.info("Authorization Sucessful");

                        List<Employee> employees = employeeService.getAllEmployees();
                        log.info("Employees retrived successfully!!");
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Success", HttpStatus.OK.value(), "Retrived all employees",
                                                        employees),
                                        HttpStatus.OK);
                }
                return responseEntity;
        }

        /**
         * Checks if the request body of an Employee object is empty.
         * 
         * @param request The Employee object to check.
         * @return {@code true} if all fields in the request object are null or zero,
         *         {@code false} otherwise.
         */
        private boolean isRequestBodyEmpty(Employee request) {
                // Check if all fields in the request object are null or zero
                return request.getFirstname() == null && request.getFullname() == null && request.getDob() == null
                                && request.getDoj() == null && request.getSalary() == 0
                                && request.getDepartment() == null && request.getRank() == null
                                && request.getReportingOfficer() == null && request.getClientRequestId() == null;
        }

        /**
         * Authorizes access based on the provided username and password in the headers.
         * 
         * @param headers The headers containing the username and password for
         *                authorization.
         * @return {@code true} if the provided username and password match the
         *         authorized credentials, {@code false} otherwise.
         */
        private boolean isAuthorize(Map<String, String> headers) {
                String authUsername = headers.get("username");
                String authPassword = headers.get("password");

                return userName.equals(authUsername) && password.equals(authPassword);
        }


}
