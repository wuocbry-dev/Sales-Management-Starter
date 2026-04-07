package com.yourcompany.salesmanagement.module.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        String password,

        @NotBlank(message = "Store name is required")
        String storeName,

        @NotBlank(message = "Business type is required")
        String businessType
) {}

