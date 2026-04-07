package com.yourcompany.salesmanagement.module.returnorder.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record CreateReturnOrderRequest(
        @NotNull(message = "Sales order id is required")
        Long salesOrderId,

        @Valid
        @NotEmpty(message = "Items are required")
        List<CreateReturnOrderItemRequest> items,

        @NotNull(message = "Refund amount is required")
        @DecimalMin(value = "0", message = "Refund amount must be >= 0")
        BigDecimal refundAmount,

        String notes
) {}

