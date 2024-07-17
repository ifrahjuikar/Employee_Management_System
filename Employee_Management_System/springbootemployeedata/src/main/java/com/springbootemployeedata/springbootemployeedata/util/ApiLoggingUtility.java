package com.springbootemployeedata.springbootemployeedata.util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.springbootemployeedata.springbootemployeedata.Entity.Employee;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.OffsetDateTime;

@Component
public class ApiLoggingUtility {

    private static final Logger logger = LoggerFactory.getLogger(ApiLoggingUtility.class);

    public void logApiRequest(HttpServletRequest request, HttpServletResponse response, Employee employee, Exception ex) {
        try {
            ApiLogDetails logDetails = extractLogDetails(request, response, employee, ex);

            // Log the API request with structured logging
            logger.info("timestamp={} | user={} | api={} | status={} | error_code={} | error_message={} | client_reqid={} | req={} ",
                    OffsetDateTime.now(), logDetails.getEmpId(), logDetails.getApi(), logDetails.getStatus(),
                    logDetails.getErrorCode(), logDetails.getErrorMessage(), logDetails.getClientReqId(), logDetails.getRequestBody());
        } catch (Exception e) {
            // Log any exceptions that occur during logging
            logger.error("Error logging API request: {}", e.getMessage(), e);
        }
    }

    private ApiLogDetails extractLogDetails(HttpServletRequest request, HttpServletResponse response, Employee employee, Exception ex) {
        Integer empId = employee.getEmpId(); // Get employee ID
        String api = request.getRequestURI(); // Get API endpoint
        String status = String.valueOf(response.getStatus()); // Get HTTP status code
        String errorCode = (ex != null) ? "GENERAL_ERROR" : ""; // Determine error code based on exception
        String errorMessage = (ex != null) ? ex.getMessage() : ""; // Get error message from exception
        String clientReqId = request.getHeader("clientRequestId"); // Get client request ID

        // Return an instance of ApiLogDetails with all extracted information
        return new ApiLogDetails(empId != null ? empId : -1,
                api != null ? api : "UNKNOWN_API",
                status,
                errorCode,
                errorMessage,
                clientReqId != null ? clientReqId : "UNKNOWN_REQID",
                employee
        );
    }

    private static class ApiLogDetails {
        private final Integer empId;
        private final String api;
        private final String status;
        private final String errorCode;
        private final String errorMessage;
        private final String clientReqId;
        private final Employee requestBody;

        public ApiLogDetails(Integer empId, String api, String status, String errorCode, String errorMessage, String clientReqId, Employee requestBody) {
            this.empId = empId;
            this.api = api;
            this.status = status;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
            this.clientReqId = clientReqId;
            this.requestBody = requestBody;
        }

        public Integer getEmpId() {
            return empId;
        }

        public String getApi() {
            return api;
        }

        public String getStatus() {
            return status;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public String getClientReqId() {
            return clientReqId;
        }

        public Employee getRequestBody() {
            return requestBody;
        }
    }
}
