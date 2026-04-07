package com.yourcompany.salesmanagement.module.user.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateUserRequest(
        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "Username is required")
        String username,

        String email,
        String phone,

        @NotBlank(message = "Password is required")
        String password,

        List<String> roleCodes
) {}

