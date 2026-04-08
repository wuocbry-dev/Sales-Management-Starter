package com.yourcompany.salesmanagement.module.returnorder.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateReturnOrderItemRequest(
        @NotNull(message = "Sales order item id is required")
        Long salesOrderItemId,

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.01", message = "Quantity must be > 0")
        BigDecimal quantity
) {}

