
package com.springbootemployeedata.springbootemployeedata.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;
// import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Employee implements Serializable {

    // Primary key, auto-generated
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer empId; // Assuming employeeId is unique

    // First name of the employee
    @Column(nullable = false)
    private String firstname;

    // Full name of the employee
    @Column(nullable = false)
    private String fullname;

    // Date of birth of the employee
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Column(nullable = false)
    private LocalDate dob;

    // Date of joining of the employee
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Column(nullable = false)
    private LocalDate doj;

    // Salary of the employee
    @Column(nullable = false)
    private int salary;

    // Reference to the reporting officer (manager) of the employee
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "reportsto")
    private Employee reportingOfficer;

    // Reference to the department the employee belongs to
    @ManyToOne
    @JoinColumn(name = "deptid")
    private Department department;

    // Reference to the rank of the employee
    @ManyToOne
    @JoinColumn(name = "rankid")
    private Rank rank;

    // Date and time when the employee record was created
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    // Date and time when the employee record was last updated
    @Column(nullable = false)
    private Date updatedAt;

    // Client request ID associated with the employee record
    @Column(nullable = true)
    private String clientRequestId;
}
