package com.yourcompany.salesmanagement.module.salesorder.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateSalesOrderRequest(
        @NotNull(message = "Branch is required")
        Long branchId,

        Long customerId,

        String notes,

        @Valid
        @NotEmpty(message = "Items are required")
        List<CreateSalesOrderItemRequest> items
) {}

