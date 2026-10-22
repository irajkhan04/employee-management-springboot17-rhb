package com.example.assignment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record EmployeePatchRequest(
        @Size(min = 1, max = 120) String name,
        @Email @Size(max = 180) String email,
        Long departmentId,
        @DecimalMin("0.00") BigDecimal salary
) {
}
