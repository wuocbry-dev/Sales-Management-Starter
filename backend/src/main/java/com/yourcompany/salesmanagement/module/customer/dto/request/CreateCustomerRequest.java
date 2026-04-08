package com.yourcompany.salesmanagement.module.customer.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CreateCustomerRequest(
        @NotBlank(message = "Customer code is required")
        String customerCode,

        @NotBlank(message = "Full name is required")
        String fullName,

        String phone,
        String email,
        String gender,
        LocalDate dateOfBirth,
        String address
) {}

