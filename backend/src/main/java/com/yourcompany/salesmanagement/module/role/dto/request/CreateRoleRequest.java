package com.yourcompany.salesmanagement.module.role.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateRoleRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Code is required")
        String code,

        String description,

        @NotEmpty(message = "permissionCodes must not be empty")
        List<String> permissionCodes
) {}

