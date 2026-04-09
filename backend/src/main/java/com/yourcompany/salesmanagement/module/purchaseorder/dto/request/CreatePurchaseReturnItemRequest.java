package com.yourcompany.salesmanagement.module.purchaseorder.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreatePurchaseReturnItemRequest(
        @NotNull(message = "purchaseOrderItemId is required")
        Long purchaseOrderItemId,

        @NotNull(message = "quantity is required")
        @DecimalMin(value = "0.01", message = "quantity must be > 0")
        BigDecimal quantity
) {}

