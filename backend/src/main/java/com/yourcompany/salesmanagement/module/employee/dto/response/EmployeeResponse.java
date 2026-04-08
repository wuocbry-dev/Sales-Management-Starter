package com.yourcompany.salesmanagement.module.employee.dto.response;

public record EmployeeResponse(
        Long id,
        Long branchId,
        Long userId,
        String employeeCode,
        String fullName,
        String status
) {}

