package com.example.assignment.dto;

import java.math.BigDecimal;

public record EmployeeResponse(
        Long id,
        String name,
        String email,
        Long departmentId,
        String departmentName,
        BigDecimal salary
) {
}
