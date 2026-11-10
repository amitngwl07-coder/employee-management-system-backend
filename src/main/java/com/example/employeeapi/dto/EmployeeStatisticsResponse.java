
package com.example.employeeapi.dto;

public record EmployeeStatisticsResponse(
        long totalEmployees,
        long activeEmployees,
        long inactiveEmployees
) {
}
