package com.yourcompany.salesmanagement.module.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
        @NotBlank(message = "Full name is required")
        String fullName,
        String email,
        String phone,
        String status
) {}

