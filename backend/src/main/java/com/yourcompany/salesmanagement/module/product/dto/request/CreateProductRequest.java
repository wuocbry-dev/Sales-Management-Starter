package com.yourcompany.salesmanagement.module.product.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductRequest(
        @NotBlank(message = "Code is required")
        String code,

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Category is required")
        String category,

        @NotNull(message = "Price is required")
        @Min(value = 0, message = "Price must be >= 0")
        Double price,

        @NotNull(message = "Stock is required")
        @Min(value = 0, message = "Stock must be >= 0")
        Integer stock
) {}
