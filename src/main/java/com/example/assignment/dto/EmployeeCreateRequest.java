package com.example.assignment.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record EmployeeCreateRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email @Size(max = 180) String email,
        @NotNull @Positive Long departmentId,
        @NotNull @DecimalMin("0.00") BigDecimal salary
) {
}
