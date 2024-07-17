package com.springbootemployeedata.springbootemployeedata.serviceimplementation;

import java.awt.Color;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springbootemployeedata.springbootemployeedata.Entity.Employee;
import com.springbootemployeedata.springbootemployeedata.Entity.EmployeeShadow;
import com.springbootemployeedata.springbootemployeedata.Exception.EmployeeNotFoundException;
import com.springbootemployeedata.springbootemployeedata.repository.EmployeeRepository;
import com.springbootemployeedata.springbootemployeedata.repository.EmployeeShadowRepository;
import com.springbootemployeedata.springbootemployeedata.service.EmployeeService;

import jakarta.servlet.http.HttpServletResponse;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.IndexedColors;

@Service
public class EmployeeServiceImp implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeShadowRepository employeeShadowRepository;

    /**
     * Adds a new employee to the database.
     * 
     * @param employee The employee to add.
     * @throws EmployeeServiceException If an error occurs while adding the
     *                                  employee.
     */
    @Override
    public void addEmployee(Employee employee) {
        employee.setCreatedAt(new Date());
        employee.setUpdatedAt(new Date());
        
        employeeRepository.save(employee);

    }

    /**
     * Retrieves a list of all employees with their IDs and full names.
     * 
     * @return A list containing employee IDs and full names.
     */
    @Override
    public List<List<Object>> listAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        List<List<Object>> employeeList = new ArrayList<>();
        for (Employee employee : employees) {
            List<Object> employeeInfo = new ArrayList<>();
            employeeInfo.add(employee.getEmpId());
            employeeInfo.add(employee.getFullname());
            employeeList.add(employeeInfo);
        }
        return employeeList;
    }

    /**
     * Retrieves a list of employees using a SQL query.
     * 
     * @return A list of employees based on the SQL query.
     */
    @Override
    public List<List<Object>> listEmployeesByUsingSqlQuery() {
        return employeeRepository.listEmployeesByUsingSqlQuery();
    }

    /**
     * Finds employees whose full names contain the provided filter string.
     * 
     * @param filter The filter string to search for in employee full names.
     * @return A list of employees matching the filter.
     * @throws EmployeeNotFoundException If no employees match the provided filter.
     */
    @Override
    public List<Employee> findByFullnameContainingIgnoreCase(String filter) {
        List<Employee> filteredEmployee = employeeRepository.findByFullnameContainingIgnoreCase1(filter);

        if (filteredEmployee.isEmpty()) {
            throw new EmployeeNotFoundException("No employees found with the provided filter" + filter);
        }

        return filteredEmployee;
    }

    /**
     * Finds employees whose full names contain the provided filter string.
     * 
     * @param fullname The fullname string to search for in employee full names.
     * @return A list of employees matching the filter.
     * @throws EmployeeNotFoundException If no employees match the provided filter.
     */
    @Override
    public List<Employee> findEmployeesByFullName(String fullName) {
        List<Employee> filteredEmployee = employeeRepository.findByFullnameContainingIgnoreCase(fullName);
        if (filteredEmployee.isEmpty()) {
            throw new EmployeeNotFoundException("No employees found with the provided filter"+ fullName);
        }
        return filteredEmployee;
    }

    /**
     * Retrieves details of an employee by their ID.
     * 
     * @param empid The ID of the employee.
     * @return Details of the employee.
     * @throws EmployeeNotFoundException If no employee is found with the provided
     *                                   ID.
     */
    @Override
    public Object getEmployeeDetails(int empid) {
        Map<String, Object> employeeDetailsMap = employeeRepository.findEmployeeDetailsById(empid);
        if (employeeDetailsMap.isEmpty()) {
            throw new EmployeeNotFoundException("Employee not found with empid "+ empid);
        } else {
            return employeeDetailsMap;
        }
    }

    /**
     * Updates an existing employee record.
     * 
     * @param empid The ID of the employee to update.
     * @param emp   The updated employee object.
     * @return The updated employee object.
     * @throws EmployeeNotFoundException If no employee is found with the provided
     *                                   ID.
     */
    @Override
    public Employee updateEmployee(Employee emp) {
        Integer empid = emp.getEmpId(); // Assuming there's a getId() method in Employee class
        Employee existingEmployee = employeeRepository.findById(empid)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee Not Found with empid: " + empid));

        copyEmployeeToShadow(existingEmployee);

        if (emp.getFirstname() != null) {
            existingEmployee.setFirstname(emp.getFirstname());
        }
        if (emp.getFullname() != null) {
            existingEmployee.setFullname(emp.getFullname());
        }
        if (emp.getDob() != null) {
            existingEmployee.setDob(emp.getDob());
        }
        if (emp.getDoj() != null) {
            existingEmployee.setDoj(emp.getDoj());
        }
        if (emp.getSalary() != 0) {
            existingEmployee.setSalary(emp.getSalary());
        }
        if (emp.getDepartment() != null) {
            existingEmployee.setDepartment(emp.getDepartment());
        }
        if (emp.getRank() != null) {
            existingEmployee.setRank(emp.getRank());
        }
        if (emp.getReportingOfficer() != null) {
            existingEmployee.setReportingOfficer(emp.getReportingOfficer());
        }
        if (emp.getClientRequestId() != null) {
            existingEmployee.setClientRequestId(emp.getClientRequestId());
        }
        // Copy employee record to employee_shadow table before update

        // Save the updated employee record
        return employeeRepository.save(existingEmployee);
    }

    /**
     * Deletes an employee record.
     * 
     * @param empid The ID of the employee to delete.
     * @return The deleted employee object.
     * @throws EmployeeNotFoundException If no employee is found with the provided
     *                                   ID.
     */
    @Override
    public Employee deleteEmployee(int empid) {
        Employee existingEmployee = employeeRepository.findById(empid)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee Not Found with empid: "+ empid));

        // Copy employee record to employee_shadow table before delete
        copyEmployeeToShadow(existingEmployee);

        // Delete employee
        employeeRepository.delete(existingEmployee);

        return existingEmployee;
    }

    /**
     * Copies an employee's data to the shadow table before updating or deleting.
     * 
     * @param employee The employee whose data will be copied.
     */
    private void copyEmployeeToShadow(Employee employee) {
        EmployeeShadow employeeShadow = new EmployeeShadow();
        BeanUtils.copyProperties(employee, employeeShadow);
        employeeShadowRepository.save(employeeShadow);
    }

    /**
     * Retrieves an employee by their ID.
     * 
     * @param empid The ID of the employee to retrieve.
     * @return The employee object.
     * @throws EmployeeNotFoundException If no employee is found with the provided
     *                                   ID.
     */
    @Override
    public Employee getOneEmployeeById(Integer empid) {
        Employee employee = employeeRepository.findById(empid)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee Not Found with empid: " + empid));
        return employee;

    }

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Generates a PDF document containing employee data and writes it to the
     * HttpServletResponse output stream.
     *
     * @param empList  The list of Employee objects to include in the PDF.
     * @param response The HttpServletResponse object to write the PDF content to.
     * @throws DocumentException If an error occurs while creating the PDF document.
     * @throws IOException       If an error occurs while writing the PDF content to
     *                           the response output stream.
     */
    @Override
    public void generateEmployeePdf(List<Employee> empList, HttpServletResponse response)
            throws DocumentException, IOException {
        // Creating the Object of Document
        Document document = new Document(PageSize.A4);

        // Getting instance of PdfWriter
        PdfWriter.getInstance(document, response.getOutputStream());

        // Opening the created document to change it
        document.open();

        // Creating font for the title
        Font fontTitle = new Font(Font.HELVETICA, 20, Font.BOLD);

        // Creating paragraph for the title
        Paragraph paragraph1 = new Paragraph("List of Employees", fontTitle);
        paragraph1.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(paragraph1);

        // Creating a table with 10 columns
        PdfPTable table = new PdfPTable(10);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 1, 3, 4, 4, 4, 3, 4, 2, 2, 3 });
        table.setSpacingBefore(5);

        // Creating font for the table header
        Font fontHeader = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);

        // Creating table cells for the header
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(new Color(0, 100, 255));
        cell.setPadding(5);

        // Adding header cells
        String[] headers = { "Emp ID", "First Name", "Full Name", "DOB", "DOJ", "Salary", "Reports To", "Dept ID",
                "Rank ID", "Create Date" };
        for (String header : headers) {
            cell.setPhrase(new Phrase(header, fontHeader));
            table.addCell(cell);
        }
        // Iterating the list of employees and adding details to the table
        for (Employee emp : empList) {
            table.addCell(String.valueOf(emp.getEmpId()));
            table.addCell(emp.getFirstname());
            table.addCell(emp.getFullname());
            table.addCell(emp.getDob().format(dateFormatter));
            table.addCell(emp.getDoj().format(dateFormatter));
            table.addCell(String.valueOf(emp.getSalary()));
            table.addCell(emp.getReportingOfficer() != null ? emp.getReportingOfficer().getFirstname() : null);
            table.addCell(emp.getDepartment() != null ? String.valueOf(emp.getDepartment().getDeptId()) : ""); // Check
                                                                                                               // for
                                                                                                               // null
            table.addCell(emp.getRank() != null ? String.valueOf(emp.getRank().getRankId()) : ""); // Check for null
            table.addCell(String.valueOf(emp.getCreatedAt()));
        }

        // Adding the created table to the document
        document.add(table);

        // Closing the document
        document.close();
    }

    /**
     * Generates an Excel spreadsheet containing employee data and writes it to the
     * HttpServletResponse output stream.
     *
     * @param empList  The list of Employee objects to include in the Excel
     *                 spreadsheet.
     * @param response The HttpServletResponse object to write the Excel content to.
     * @throws IOException If an error occurs while writing the Excel content to the
     *                     response output stream.
     */
    @Override
    public void generateEmployeeExcel(List<Employee> empList, HttpServletResponse response) throws IOException {
        // Creating the Workbook
        Workbook workbook = new XSSFWorkbook();
        // Creating the Sheet
        Sheet sheet = workbook.createSheet("Employees");
        // Creating the header row
        Row headerRow = sheet.createRow(0);

        // Creating cell style for header
        CellStyle headerCellStyle = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Adding header cells
        String[] headers = { "Emp ID", "First Name", "Full Name", "DOB", "DOJ", "Salary", "Reports To", "Dept ID",
                "Rank ID", "Create Date" };
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerCellStyle);
        }

        // Iterating the list of employees and adding details to the sheet
        int rowNum = 1;
        for (Employee emp : empList) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(emp.getEmpId());
            row.createCell(1).setCellValue(emp.getFirstname());
            row.createCell(2).setCellValue(emp.getFullname());
            row.createCell(3).setCellValue(emp.getDob().format(dateFormatter));
            row.createCell(4).setCellValue(emp.getDoj().format(dateFormatter));
            row.createCell(5).setCellValue(emp.getSalary());
            row.createCell(6)
                    .setCellValue(emp.getReportingOfficer() != null ? emp.getReportingOfficer().getFirstname() : "");
            row.createCell(7)
                    .setCellValue(emp.getDepartment() != null ? String.valueOf(emp.getDepartment().getDeptId()) : "");
            row.createCell(8).setCellValue(emp.getRank() != null ? String.valueOf(emp.getRank().getRankId()) : "");
            row.createCell(9).setCellValue(emp.getCreatedAt().toString());
        }

        // Resizing columns to fit the content
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Writing the workbook to the response output stream
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /*
     * Retrieves a list of all employees.
     * 
     * @return A list of all employees.
     */
    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }



}
