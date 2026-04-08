package com.yourcompany.salesmanagement.module.category.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(
        @NotBlank(message = "Name is required")
        String name
) {}

