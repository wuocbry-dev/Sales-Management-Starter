package com.yourcompany.salesmanagement.module.product.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateProductRequest(
        @NotBlank(message = "SKU is required")
        String sku,

        @NotBlank(message = "Name is required")
        String name,

        Long categoryId,

        Long supplierId,

        @NotNull(message = "Selling price is required")
        @Min(value = 0, message = "Selling price must be >= 0")
        Double sellingPrice,

        Boolean trackInventory,

        String status
) {}

