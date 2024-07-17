
package com.springbootemployeedata.springbootemployeedata.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.springbootemployeedata.springbootemployeedata.util.LoggingErrorUtility;

import org.springframework.http.converter.HttpMessageNotReadableException;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ControllerAdvice
public class GlobalExceptionHandler {
    // private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handles HttpMessageNotReadableException, typically thrown when the JSON
    // request is malformed.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
            WebRequest request) {
        LoggingErrorUtility.errorlogging("http.message.not.readable.exception");
        ApiResponse response = new ApiResponse("Error", HttpStatus.BAD_REQUEST.value(), "Malformed JSON request", null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handles DataIntegrityViolationException, usually thrown when there is a
    // database constraint violation.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex,
            WebRequest request) {
        LoggingErrorUtility.errorlogging("data.integrity.violation.exception");
        ApiResponse response = new ApiResponse("Error", HttpStatus.BAD_REQUEST.value(), "Data integrity violation",
                null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handles MethodArgumentNotValidException, which is thrown when validation on
    // an argument annotated with @Valid fails.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            WebRequest request) {
        LoggingErrorUtility.errorlogging("method.argument.not.valid");
        ApiResponse response = new ApiResponse("Error", HttpStatus.BAD_REQUEST.value(),
                "Validation error: " + ex.getBindingResult().toString(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handles NoResourceFoundException, thrown when a requested resource is not
    // found.
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse> handleNoHandlerFoundException(NoResourceFoundException ex, WebRequest request) {
        LoggingErrorUtility.errorlogging("no.resource.found.exception",ex.getMessage());
        ApiResponse response = new ApiResponse("Error", HttpStatus.NOT_FOUND.value(), "Wrong URI requested", null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Handles EmployeeNotFoundException, thrown when an employee is not found.
    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ApiResponse> handleEmployeeNotFoundException(EmployeeNotFoundException ex,
            WebRequest request) {
        LoggingErrorUtility.errorlogging("employee.not.found.exception", ex.getMessage());
        ApiResponse response = new ApiResponse("Error", HttpStatus.NOT_FOUND.value(),
                "Employee not found: " + ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Handles HttpRequestMethodNotSupportedException, thrown when an unsupported
    // HTTP method is used.
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex,
            WebRequest request) {
        LoggingErrorUtility.errorlogging("method.not.allowed", ex.getMessage());
        ApiResponse response = new ApiResponse("Error", HttpStatus.METHOD_NOT_ALLOWED.value(),
                "Unsupported HTTP method used: " + ex.getMethod(), null);
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // Handles all other exceptions that are not explicitly handled by other
    // methods.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleAllExceptions(Exception ex, WebRequest request) {
        LoggingErrorUtility.errorlogging("handle.all.exceptions");
        ApiResponse response = new ApiResponse("Error", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error", null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
