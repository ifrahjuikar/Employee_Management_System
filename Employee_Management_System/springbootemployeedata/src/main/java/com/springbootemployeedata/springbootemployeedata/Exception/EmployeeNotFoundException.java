package com.springbootemployeedata.springbootemployeedata.Exception;


// Custom exception class to represent when an employee is not found.
// This exception is annotated with @ResponseStatus to return a 404 status code to the client.
public class EmployeeNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 7428051251365675318L;

    // Constructs a new EmployeeNotFoundException with the specified detail message.
    public EmployeeNotFoundException(String message) {
        super(message);
    }
}
