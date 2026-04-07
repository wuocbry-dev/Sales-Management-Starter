package com.yourcompany.salesmanagement.module.user.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AssignRolesRequest(
        @NotEmpty(message = "roleCodes must not be empty")
        List<String> roleCodes
) {}

