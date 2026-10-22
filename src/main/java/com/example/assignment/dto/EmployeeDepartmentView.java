package com.example.assignment.dto;

public record EmployeeDepartmentView(
        Long employeeId,
        String employeeName,
        String email,
        String departmentName
) {
}
