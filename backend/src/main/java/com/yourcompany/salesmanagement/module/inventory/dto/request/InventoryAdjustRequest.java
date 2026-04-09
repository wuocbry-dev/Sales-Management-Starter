package com.yourcompany.salesmanagement.module.inventory.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record InventoryAdjustRequest(
        @NotNull(message = "Branch is required")
        Long branchId,

        @NotNull(message = "Product is required")
        Long productId,

        Long variantId,

        @NotNull(message = "Delta quantity is required")
        BigDecimal deltaQuantity,

        String reason
) {}

