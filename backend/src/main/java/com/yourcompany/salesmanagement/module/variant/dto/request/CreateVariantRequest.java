package com.yourcompany.salesmanagement.module.variant.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateVariantRequest(
        @NotBlank(message = "SKU is required")
        String sku,

        String barcode,

        @NotBlank(message = "Variant name is required")
        String variantName,

        String option1Name,
        String option1Value,
        String option2Name,
        String option2Value,

        @NotNull(message = "Cost price is required")
        @Min(value = 0, message = "Cost price must be >= 0")
        Double costPrice,

        @NotNull(message = "Selling price is required")
        @Min(value = 0, message = "Selling price must be >= 0")
        Double sellingPrice
) {}

