package com.yourcompany.salesmanagement.module.employee.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateEmployeeRequest(
        Long branchId,
        Long userId,

        @NotBlank(message = "Employee code is required")
        String employeeCode,

        @NotBlank(message = "Full name is required")
        String fullName
) {}

