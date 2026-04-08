package com.yourcompany.salesmanagement.module.customer.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record UpdateCustomerRequest(
        @NotBlank(message = "Full name is required")
        String fullName,
        String phone,
        String email,
        String gender,
        LocalDate dateOfBirth,
        String address,
        String status
) {}

