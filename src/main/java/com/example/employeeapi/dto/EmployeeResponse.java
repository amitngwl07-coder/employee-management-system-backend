
package com.example.employeeapi.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmployeeResponse {

    private long id;
    private String employeeCode;
    private String name;
    private String email;
    private String department;
    private LocalDate joiningDate;
    private String status;
    private LocalDateTime createdOn;

    public EmployeeResponse(
            long id,
            String employeeCode,
            String name,
            String email,
            String department,
            LocalDate joiningDate,
            String status,
            LocalDateTime createdOn
    ) {
        this.id = id;
        this.employeeCode = employeeCode;
        this.name = name;
        this.email = email;
        this.department = department;
        this.joiningDate = joiningDate;
        this.status = status;
        this.createdOn = createdOn;
    }

    public long getId() {
        return id;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDepartment() {
        return department;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }
}
