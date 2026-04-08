package com.yourcompany.salesmanagement.module.purchaseorder.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AddPurchaseOrderItemRequest(
        @NotNull(message = "Product is required")
        Long productId,

        Long variantId,

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.01", message = "Quantity must be > 0")
        BigDecimal quantity,

        @NotNull(message = "Cost price is required")
        @DecimalMin(value = "0", message = "Cost price must be >= 0")
        BigDecimal costPrice
) {}
