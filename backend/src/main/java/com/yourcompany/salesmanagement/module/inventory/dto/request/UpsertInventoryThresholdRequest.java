package com.yourcompany.salesmanagement.module.inventory.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpsertInventoryThresholdRequest(
        @NotNull(message = "Branch is required")
        Long branchId,

        @NotNull(message = "Product is required")
        Long productId,

        Long variantId,

        @NotNull(message = "minQuantity is required")
        @DecimalMin(value = "0.00", inclusive = true, message = "minQuantity must be >= 0")
        BigDecimal minQuantity,

        @DecimalMin(value = "0.00", inclusive = true, message = "maxQuantity must be >= 0")
        BigDecimal maxQuantity
) {}

