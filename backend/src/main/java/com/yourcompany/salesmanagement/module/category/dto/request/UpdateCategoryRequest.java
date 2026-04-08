package com.yourcompany.salesmanagement.module.category.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateCategoryRequest(
        @NotBlank(message = "Name is required")
        String name,
        String status
) {}

